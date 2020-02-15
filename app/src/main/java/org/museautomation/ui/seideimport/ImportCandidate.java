package org.museautomation.ui.seideimport;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import javafx.scene.paint.*;
import org.museautomation.core.*;
import org.museautomation.core.resource.*;
import org.museautomation.core.steptest.*;
import org.museautomation.seleniumide.*;
import org.slf4j.*;

import java.io.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ImportCandidate
	{
	public ImportCandidate(File file, int index, MuseProject muse_project)
		{
		_file = file;
		if (LAST_FILE == null || !LAST_FILE.equals(file))
			{
			try
				{
				LAST_PROJECT = null;
				LAST_FILE = file;
				FileInputStream instream = new FileInputStream(file);
				LAST_PROJECT = new ObjectMapper().readValue(instream, SideProject.class);
				}
			catch (FileNotFoundException e)
				{
				LOG.error("Did the file disappear after the user selected it?", e);
				_failure_message = "Unable to open/read file.";
				_status = Status.Fail;
				}
			catch (JsonParseException e)
				{
				LOG.error("Unable to read a test from file", e);
				_failure_message = "Unable to parse the file.";
				_status = Status.Fail;
				}
			catch (IOException e)
				{
				LOG.error("I/O error", e);
				_failure_message = "I/O error while reading file.";
				_status = Status.Fail;
				}
			}

		if (LAST_PROJECT == null)
			return;

		if (index >= LAST_PROJECT.getTests().length)
			throw new IllegalArgumentException("no more tests to convert");

		final SideTest test = LAST_PROJECT.getTests()[index];
		_resource_id = new FilenameValidator().suggestValidName(test.getName());
		SideTestConverter converter = new SideTestConverter();
		_result = converter.convert(test, LAST_PROJECT);
		setStatus(muse_project);

		checkStatus();
		}

	public ImportCandidate(File file, MuseProject project)
		{
		_file = file;
		try (FileInputStream instream = new FileInputStream(file))
			{
			_resource_id = _file.getName().substring(0, _file.getName().lastIndexOf('.'));
			TestConverter converter = new TestConverter(instream);
			_result = converter.convert();
			setStatus(project);
			}
		catch (FileNotFoundException e)
			{
			LOG.error("Did the file disappear after the user selected it?", e);
			_failure_message = "Unable to open/read file.";
			_status = Status.Fail;
			}
		catch (UnsupportedError e)
			{
			LOG.error(e.getMessage());
			_failure_message = "File contains one or more unsupported constructs: " + e.getMessage();
			_status = Status.Fail;
			}
		catch (IOException e)
			{
			LOG.error("I/O error", e);
			_failure_message = "I/O error while reading file.";
			_status = Status.Fail;
			}
		catch (Exception e)
			{
			LOG.error("Unable to read a test from file", e);
			_failure_message = "Unable to parse the file.";
			_status = Status.Fail;
			}

		checkStatus();
		}

	private void checkStatus()
		{
		switch (_status)
			{
			case Ready:
			case Warning:
				_enabled = true;
				break;
			case DuplicateId:
				_enabled = false;
				break;
			case Fail:
				_enabled = null;
			}
		}

	private void setStatus(MuseProject project)
		{
		boolean duplicate = !(project.getResourceStorage().findResource(getResourceId()) == null);
		if (_result._success)
			{
			if (!duplicate)
				_status = Status.Ready;
			else
				_status = Status.DuplicateId;
			}
		else
			{
			if (duplicate)
				_status = Status.DuplicateId;
			else
				_status = Status.Warning;
			}
		}

	public String getResourceId()
		{
		return _resource_id;
		}

	public Status getStatus()
		{
		return _status;
		}

	public SteppedTest getTest()
		{
		SteppedTest test = _result._test;
		test.setId(getResourceId());
		return test;
		}

	@SuppressWarnings("unused") // used via reflection in ImportCandidateTable
	public String getComments()
		{
		switch (_status)
			{
			case Fail:
				return _failure_message;
			case Warning:
				return String.format("Test has %d steps that may require further configuration or a software update", _result._errors.size());
			case DuplicateId:
				return String.format("Test id %s already exists in project, importing will overwrite the resource.", getResourceId());
			}
		return "";
		}

	public Boolean getEnabled()
		{
		return _enabled;
		}

	public void setEnabled(boolean enabled)
		{
		if (_enabled == null)
			throw new IllegalStateException("Cannot enable this ImportCandidate");
		_enabled = enabled;
		if (_listener != null)
			_listener.changed();
		}

	public int getUnrecognizedCommandCount()
		{
		return _result._errors.size();
		}

	public void onChange(OnChangeListener listener)
		{
		_listener = listener;
		}

	public File getFile()
		{
		return _file;
		}

	File _file;
	private Status _status = Status.Fail;
	private ConversionResult _result;
	private Boolean _enabled;
	private String _failure_message = "Not an HTML file";
	private OnChangeListener _listener;
	private String _resource_id;

	public enum Status
		{
			Ready("FA:CHECK", Color.GREEN),
			DuplicateId("FA:QUESTION", Color.ORANGE),
			Warning("FA:QUESTION", Color.ORANGE),
			Fail("FA:TIMES", Color.RED);

		Status(String glyph_name, Color color)
			{
			_glyph_name = glyph_name;
			_color = color;
			}

		public String getGlyphName()
			{
			return _glyph_name;
			}

		public Color getGlyphColor()
			{
			return _color;
			}

		private final String _glyph_name;
		private final Color _color;
		}

	public interface OnChangeListener
		{
		void changed();
		}

	private final static Logger LOG = LoggerFactory.getLogger(ImportCandidate.class);

	private static File LAST_FILE = null;
	private static SideProject LAST_PROJECT = null;
	}
