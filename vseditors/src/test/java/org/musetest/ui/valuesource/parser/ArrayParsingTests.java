package org.musetest.ui.valuesource.parser;

import org.junit.*;
import org.musetest.builtins.value.*;
import org.musetest.builtins.value.collection.*;
import org.musetest.core.*;
import org.musetest.core.project.*;
import org.musetest.core.values.*;
import org.musetest.parsing.valuesource.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ArrayParsingTests
	{
	@Test
	public void emptyArray() throws ExpressionParsingException
		{
		ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("[]");
		Assert.assertEquals(ListSource.TYPE_ID, configuration.getType());
		Assert.assertNull(configuration.getSourceList());
		}

	@Test
	public void arrayOfOne() throws ExpressionParsingException
	    {
	    ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("[\"first\"]");
	    Assert.assertEquals(ListSource.TYPE_ID, configuration.getType());
	    Assert.assertEquals(1, configuration.getSourceList().size());
	    Assert.assertEquals("first", configuration.getSource(0).getValue());
	    }

	@Test
	public void arrayOfTwo() throws ExpressionParsingException
	    {
	    ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("[\"first\",true]");
	    Assert.assertEquals(ListSource.TYPE_ID, configuration.getType());
	    Assert.assertEquals(2, configuration.getSourceList().size());
	    Assert.assertEquals("first", configuration.getSource(0).getValue());
	    Assert.assertEquals(true, configuration.getSource(1).getValue());
	    }

    @Test
    public void arrayAccess() throws ExpressionParsingException
        {
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("$\"list\"[$\"index\"]");
        Assert.assertEquals(GetItemFromCollection.TYPE_ID, configuration.getType());

        ValueSourceConfiguration collection_source = configuration.getSource(GetItemFromCollection.COLLECTION_PARAM);
        Assert.assertEquals(VariableValueSource.TYPE_ID, collection_source.getType());
        Assert.assertEquals("list", collection_source.getSource().getValue());

        ValueSourceConfiguration selector_source = configuration.getSource(GetItemFromCollection.SELECTOR_PARAM);
        Assert.assertEquals(VariableValueSource.TYPE_ID, selector_source.getType());
        Assert.assertEquals("index", selector_source.getSource().getValue());
        }


    private MuseProject _project = new SimpleProject();
	}


