package org.museautomation.ui.valuesource.parser;

import org.junit.jupiter.api.*;
import org.museautomation.parsing.valuesource.*;
import org.museautomation.builtins.value.*;
import org.museautomation.builtins.value.collection.*;
import org.museautomation.core.*;
import org.museautomation.core.project.*;
import org.museautomation.core.values.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class ArrayParsingTests
	{
	@Test
    void emptyArray() throws ExpressionParsingException
		{
		ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("[]");
		Assertions.assertEquals(ListSource.TYPE_ID, configuration.getType());
		Assertions.assertNull(configuration.getSourceList());
		}

	@Test
    void arrayOfOne() throws ExpressionParsingException
	    {
	    ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("[\"first\"]");
	    Assertions.assertEquals(ListSource.TYPE_ID, configuration.getType());
	    Assertions.assertEquals(1, configuration.getSourceList().size());
	    Assertions.assertEquals("first", configuration.getSource(0).getValue());
	    }

	@Test
    void arrayOfTwo() throws ExpressionParsingException
	    {
	    ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("[\"first\",true]");
	    Assertions.assertEquals(ListSource.TYPE_ID, configuration.getType());
	    Assertions.assertEquals(2, configuration.getSourceList().size());
	    Assertions.assertEquals("first", configuration.getSource(0).getValue());
	    Assertions.assertEquals(true, configuration.getSource(1).getValue());
	    }

    @Test
    void arrayAccess() throws ExpressionParsingException
        {
        ValueSourceConfiguration configuration = new ValueSourceExpressionParser(_project).parse("$\"list\"[$\"index\"]");
        Assertions.assertEquals(GetItemFromCollection.TYPE_ID, configuration.getType());

        ValueSourceConfiguration collection_source = configuration.getSource(GetItemFromCollection.COLLECTION_PARAM);
        Assertions.assertEquals(VariableValueSource.TYPE_ID, collection_source.getType());
        Assertions.assertEquals("list", collection_source.getSource().getValue());

        ValueSourceConfiguration selector_source = configuration.getSource(GetItemFromCollection.SELECTOR_PARAM);
        Assertions.assertEquals(VariableValueSource.TYPE_ID, selector_source.getType());
        Assertions.assertEquals("index", selector_source.getSource().getValue());
        }

    private MuseProject _project = new SimpleProject();
	}