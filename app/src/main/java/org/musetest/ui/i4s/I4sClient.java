package org.musetest.ui.i4s;

import com.fasterxml.jackson.jaxrs.json.*;
import com.ide4selenium.web.ws.users.*;
import org.musetest.extensions.*;
import org.musetest.ui.settings.*;
import org.slf4j.*;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class I4sClient
    {
    public static I4sClient get()
        {
        if (INSTANCE == null)
            INSTANCE = new I4sClient();
        return INSTANCE;
        }

    public I4sClient()
        {
        Client client = ClientBuilder.newClient();
        client.register(JacksonJsonProvider.class);
        String hostname = EnvironmentSettings.get().getWebsiteHostname();
        _target = client.target("http://" + hostname).path("ws");

        new Thread(() ->
            {
            try
                {
                UserActivity activity = new UserActivity();
                activity.setUserId(UserSettings.get().getUserId());
                Response response = _target.path("user").path("activity").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(activity, MediaType.APPLICATION_JSON_TYPE));
                if (!(response.getStatus() == Response.Status.OK.getStatusCode()))
                    LOG.error("I4s did not accept the user activity.");
                }
            catch (Exception e)
                {
                LOG.error("I4sClient is unable to contact the site: " + e.getMessage());
                }
            }).start();
        }

    public boolean ping()
        {
        try
            {
            PingResponse response = _target.path("ping").request(MediaType.APPLICATION_JSON_TYPE).get(PingResponse.class);
            return response.isGood();
            }
        catch (Exception e)
            {
            LOG.error("I4sClient is unable to ping the site: " + e.getMessage());
            return false;
            }
        }

    public List<ExtensionInfo> getAvailableExtensions()
        {
        try
            {
            return _target.path("extension/list").request(MediaType.APPLICATION_JSON).get(new GenericType<List<ExtensionInfo>>(){});
            }
        catch (Exception e)
            {
            LOG.error("I4sClient unable to retrieve list of available extensions: " + e.getMessage());
            return new ArrayList<>();
            }
        }

    private final WebTarget _target;

    private static I4sClient INSTANCE;

    final static Logger LOG = LoggerFactory.getLogger(I4sClient.class);
    }


