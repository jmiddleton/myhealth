package ar.com.tunuyan.health.json;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.xml.transform.StringSource;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * Convert xml to json.
 * 
 * @author jmiddleton
 * 
 */
public class XmlToJsonConverter {

	public static String xsltXmlToJson(Source source) throws ConverterException {
		try {

			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(new StreamSource(XmlToJsonConverter.class
					.getResourceAsStream("/META-INF/xmljson/xml-to-jsonml.xsl")));

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			transformer.transform(source, new StreamResult(baos));

			String json = baos.toString();

			return json;
		} catch (Exception e) {
			throw new ConverterException(e);
		}
	}

	public static <T> T convertXmlToObject(Source source, Class<T> type) throws ConverterException {
		try {

			String xml = convertXmlToString(source);

			XmlMapper jsonMapper = new XmlMapper();
			T record = jsonMapper.readValue(xml, type);

			return record;

		} catch (Exception e) {
			throw new ConverterException(e);
		}
	}

	public static String convertXmlToString(Source source) throws ConverterException {

		if (source instanceof StringSource) {
			return ((StringSource) source).toString();
		}

		StringWriter writer = new StringWriter();
		TransformerFactory factory = TransformerFactory.newInstance();

		try {
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, new StreamResult(writer));

			return writer.toString();
		} catch (Exception e) {
			throw new ConverterException(e);
		}
	}

	public final static String readFile(String file) throws IOException {
		BufferedInputStream fin = new BufferedInputStream(XmlToJsonConverter.class.getResourceAsStream(file));
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			byte buffer[] = new byte[8192];
			int read = fin.read(buffer);
			while (read != -1) {
				bout.write(buffer, 0, read);
				read = fin.read(buffer);
			}
			return new String(bout.toByteArray());
		} finally {
			fin.close();
		}
	}
}
