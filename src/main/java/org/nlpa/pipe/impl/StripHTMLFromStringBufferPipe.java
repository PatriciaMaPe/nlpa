package org.nlpa.pipe.impl;

import com.google.auto.service.AutoService;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.TransformationPipe;
import org.bdp4j.types.Instance;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.nio.charset.Charset;
import java.util.regex.Pattern;
import org.bdp4j.pipe.Pipe;

/**
 * This pipe drops HTML tags and changes entities by their corresponding
 * character The data of the instance should contain a StringBuffer with HTML
 *
 * @author José Ramón Méndez
 */
@AutoService(Pipe.class)
@TransformationPipe()
public class StripHTMLFromStringBufferPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(StripHTMLFromStringBufferPipe.class);

    /**
     * Return the input type included the data attribute of a Instance
     *
     * @return the input type for the data attribute of the Instances processed
     */
    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of a Instance after
     * processing
     *
     * @return the datatype expected in the data attribute of a Instance after
     * processing
     */
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    /**
     * NOTE *
     *//*
	 The following lines of source code (regular expressions and isHtml method) 
	 have been extracted from the open source project Reporting Tool. Public 
	 information about it is available in:
		 https://github.com/kbss-cvut/reporting-tool
		 
	 The URL of the file used is 
		 https://github.com/kbss-cvut/reporting-tool/blob/master/src/main/java/cz/cvut/kbss/reporting/util/DetectHtml.java
     */
    /**
     * NOTE *
     */

    //Adapted from post by Phil Haack and modified to match better
    public final static String tagStart
            = "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)\\>";
    public final static String tagEnd
            = "\\</\\w+\\>";
    public final static String tagSelfClosing
            = "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)/\\>";
    public final static String htmlEntity
            = "&[a-zA-Z][a-zA-Z0-9]+;";
    public final static Pattern htmlPattern = Pattern.compile(
            "(" + tagStart + ".*" + tagEnd + ")|(" + tagSelfClosing + ")|(" + htmlEntity + ")",
            Pattern.DOTALL
    );

    /**
     * Will return true if s contains HTML markup tags or entities.
     *
     * @param s String to test
     * @return true if string contains HTML
     */
    public static boolean isHtml(String s) {
        boolean ret = false;
        if (s != null) {
            ret = htmlPattern.matcher(s).find();
        }
        return ret;
    }

    /**
     * Construct a StripHTMLFromStringBufferPipe instance
     */
    public StripHTMLFromStringBufferPipe() {
        super(new Class<?>[0], new Class<?>[0]);
    }

    /**
     * Process an Instance. This method takes an input Instance, destructively
     * modifies it in some way, and returns it. This is the method by which all
     * pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instancia procesada
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {
            StringBuffer newSb = new StringBuffer();

            String data = carrier.getData().toString();
            if (isHtml(data)) {
                Document doc = Jsoup.parse(data);
                doc.charset(Charset.forName("UTF-16"));

                String title;
                if ((title = doc.title()) != null && title.length() > 0) {
                    newSb.append(title).append("\n\n");
                }

                Elements elements = doc.getAllElements();
                for (Element element : elements) {
                    for (TextNode node : element.textNodes()) {
                        newSb.append(StringEscapeUtils.unescapeHtml4(node.text())).append("\n");
                    }
                }

                carrier.setData(newSb);

            } else {
                logger.info("HTML not found for instance " + carrier.toString());
            }
        }

        return carrier;
    }
}