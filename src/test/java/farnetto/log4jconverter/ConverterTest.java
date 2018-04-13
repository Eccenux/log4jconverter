package farnetto.log4jconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/**
 * Tests the Converter class.
 */
public class ConverterTest
{
    /**
     * Test with a relatively complicated configuration.
     */
    @Test
    public void testConverter() throws JAXBException, ParserConfigurationException, SAXException, TemplateNotFoundException, MalformedTemplateNameException,
            ParseException, IOException, TemplateException
    {
        convert("/in/log4j.fortest.xml");
    }

    @Test
    public void simple()
    {
        convert("/in/log4j.simple.xml");
    }

    @Test
    public void simpleCompare()
    {
        convertAndCompare("simple.xml");
    }

    /**
     * @param xmlFileSuffix
     */
    private void convertAndCompare(String xmlFileSuffix)
    {
        File f = new File("target/test-out/log4j2." + xmlFileSuffix);
        if (f.exists())
        {
            if (!f.delete())
            {
                fail("could not delete " + f);
            }
        }
        try
        {
            FileOutputStream fos = new FileOutputStream(f);
            convert("/in/log4j." + xmlFileSuffix, fos);
        }
        catch (FileNotFoundException e)
        {
            fail("could not create file " + f + ", exception: " + e);
        }

        String expectedOutputFileName = "/out/log4j2." + xmlFileSuffix;
        try
        {
            Path expectedOut = Paths.get(getClass().getResource(expectedOutputFileName).toURI());
            Path actualOut = Paths.get("target/test-out", "log4j2." + xmlFileSuffix);
            List<String> expectedOutLines = Files.readAllLines(expectedOut, StandardCharsets.UTF_8);
            List<String> actualOutLines = Files.readAllLines(actualOut, StandardCharsets.UTF_8);
            assertEquals(expectedOutLines.size(), actualOutLines.size());
            int idx = 0;
            for (String expectedLine : expectedOutLines)
            {
                assertEquals(expectedLine, actualOutLines.get(idx++));
            }
        }
        catch (URISyntaxException | IOException e)
        {
            fail("could not read file " + e);
        }
    }

    @Test(expected = NullPointerException.class)
    public void nullInput()
    {
        convert(null);
    }

    /**
     * Helper method.
     */
    private void convert(String file)
    {
        convert(file, System.out);
    }

    private void convert(String file, OutputStream out)
    {
        new Converter().convert(getClass().getResourceAsStream(file), out);
    }
}