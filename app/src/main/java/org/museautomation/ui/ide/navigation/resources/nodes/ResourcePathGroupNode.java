package org.museautomation.ui.ide.navigation.resources.nodes;

import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.museautomation.core.resource.storage.*;
import org.slf4j.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ResourcePathGroupNode extends ResourceGroupNode
    {
    public ResourcePathGroupNode(MuseProject project, String[] path)
        {
        super(project);
        _path = path;
        if (path.length == 0)
            _label = "";
        else
            _label = path[path.length - 1];
        }

    @Override
    public String getTreeLabel()
        {
        return _label;
        }

    public String getPath()
        {
        StringBuilder builder = new StringBuilder();
        for (String p : _path)
            {
            if (builder.length() > 0)
                builder.append("/");
            builder.append(p);
            }
        return builder.toString();
        }

    @Override
    protected List<ResourceTreeNode> getInitialChildList()
        {
        return new ArrayList<>();
        }

    private String[] getPaths(ResourceToken<MuseResource> token)
        {
        MuseResource resource = token.getResource();
        Object meta = resource.metadata().getMetadataField(FolderIntoMemoryResourceStorage.PATH_ATTRIBUTE_NAME);
        String path = "";
        if (meta != null)
            path = meta.toString();
        if (path.length() < 1)
            return new String[0];
        return path.split("[/\\\\]");
        }

    protected PathRelationship getRelationship(String[] other)
        {
        // can't be related (could be anscestor, but don't care about those)
        if (_path.length > other.length)
            return PathRelationship.NOTAPPLICABLE;

        // same length...so either this or N/A
        if (_path.length == other.length)
            {
            if (Arrays.deepEquals(_path, other))
                return PathRelationship.THIS;
            else
                return PathRelationship.NOTAPPLICABLE;
            }

        // could be child or descendent
        for (int i = 0; i < _path.length; i++)
            {
            if (!_path[i].equals(other[i]))
                return PathRelationship.NOTAPPLICABLE;
            }

        // other path starts with this path
        if (other.length == _path.length + 1)
            return PathRelationship.CHILD;
        else
            return PathRelationship.DESCENDENT;
        }

    @Override
    protected boolean resourceAddedToProject(ResourceToken<MuseResource> added)
        {
        String[] added_paths = getPaths(added);
        PathRelationship relationship = getRelationship(added_paths);
        switch (relationship)
            {
            case NOTAPPLICABLE:
                return false;
            case THIS:
                ResourceNode node = new ResourceNode(added, getProject());
                addChild(node);
                return true;
            case CHILD:
            case DESCENDENT:
                String[] next_path = buildNextPath(added_paths);
                for (ResourceTreeNode child : getChildren())
                    if (child instanceof ResourcePathGroupNode)
                        {
                        ResourcePathGroupNode child_group = (ResourcePathGroupNode) child;
                        PathRelationship rel = child_group.getRelationship(next_path);
                        if (!rel.equals(PathRelationship.NOTAPPLICABLE))
                            return child_group.resourceAddedToProject(added);
                        }
                
                ResourcePathGroupNode new_node = new ResourcePathGroupNode(getProject(), next_path);
                new_node.resourceAddedToProject(added);
                addChild(new_node);
                return true;
            default:
                LOG.error("unsupported Relationship type: " + relationship.name());
                return false;
            }
        }

    private String[] buildNextPath(String[] added_paths)
        {
        String[] paths = Arrays.copyOf(_path, _path.length + 1);
        paths[paths.length - 1] = added_paths[paths.length - 1];
        return paths;
        }

    @Override
    protected boolean resourceRemovedFromProject(ResourceToken<MuseResource> removed)
        {
        for (ResourceTreeNode node : getChildren())
            if (node instanceof ResourceNode && ((ResourceNode) node).getResourceToken().equals(removed))
                {
                removeChild(node);
                return true;
                }
        return false;
        }

    private final String[] _path;
    private final String _label;

    protected enum PathRelationship
        {
        NOTAPPLICABLE,
        THIS,
        CHILD,
        DESCENDENT
        }

    final static Logger LOG = LoggerFactory.getLogger(ResourcePathGroupNode.class);
    }