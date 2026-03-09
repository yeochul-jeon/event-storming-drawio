package com.eventstorming.drawio.application.service;

import com.eventstorming.drawio.domain.model.Connection;
import com.eventstorming.drawio.domain.model.EventStormingBoard;
import com.eventstorming.drawio.domain.model.PostIt;
import com.eventstorming.drawio.domain.model.PostItType;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

@Component
public class DrawioXmlGenerator {

    public String generate(EventStormingBoard board) {
        try {
            Document doc = createDocument();
            Element mxfile = doc.createElement("mxfile");
            doc.appendChild(mxfile);

            Element diagram = doc.createElement("diagram");
            diagram.setAttribute("id", "event-storming");
            diagram.setAttribute("name", "Event Storming Board");
            mxfile.appendChild(diagram);

            Element mxGraphModel = doc.createElement("mxGraphModel");
            mxGraphModel.setAttribute("dx", "1422");
            mxGraphModel.setAttribute("dy", "762");
            mxGraphModel.setAttribute("grid", "1");
            mxGraphModel.setAttribute("gridSize", "10");
            mxGraphModel.setAttribute("guides", "1");
            mxGraphModel.setAttribute("tooltips", "1");
            mxGraphModel.setAttribute("connect", "1");
            mxGraphModel.setAttribute("arrows", "1");
            mxGraphModel.setAttribute("fold", "1");
            mxGraphModel.setAttribute("page", "1");
            mxGraphModel.setAttribute("pageScale", "1");
            mxGraphModel.setAttribute("pageWidth", String.valueOf((int) board.boardWidth()));
            mxGraphModel.setAttribute("pageHeight", String.valueOf((int) board.boardHeight()));
            diagram.appendChild(mxGraphModel);

            Element root = doc.createElement("root");
            mxGraphModel.appendChild(root);

            addDefaultCells(doc, root);

            for (PostIt postIt : board.postIts()) {
                addPostItCell(doc, root, postIt);
            }

            for (Connection connection : board.connections()) {
                addConnectionCell(doc, root, connection);
            }

            return toXmlString(doc);
        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException("draw.io XML 생성 실패", e);
        }
    }

    private Document createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }

    private void addDefaultCells(Document doc, Element root) {
        Element cell0 = doc.createElement("mxCell");
        cell0.setAttribute("id", "0");
        root.appendChild(cell0);

        Element cell1 = doc.createElement("mxCell");
        cell1.setAttribute("id", "1");
        cell1.setAttribute("parent", "0");
        root.appendChild(cell1);
    }

    private void addPostItCell(Document doc, Element root, PostIt postIt) {
        PostItType type = postIt.type();

        Element cell = doc.createElement("mxCell");
        cell.setAttribute("id", postIt.id());
        cell.setAttribute("value", postIt.text());
        cell.setAttribute("style", buildPostItStyle(type));
        cell.setAttribute("vertex", "1");
        cell.setAttribute("parent", "1");

        Element geometry = doc.createElement("mxGeometry");
        geometry.setAttribute("x", String.valueOf((int) postIt.position().x()));
        geometry.setAttribute("y", String.valueOf((int) postIt.position().y()));
        geometry.setAttribute("width", String.valueOf((int) postIt.width()));
        geometry.setAttribute("height", String.valueOf((int) postIt.height()));
        geometry.setAttribute("as", "geometry");
        cell.appendChild(geometry);

        root.appendChild(cell);
    }

    private String buildPostItStyle(PostItType type) {
        return "rounded=1;whiteSpace=wrap;html=1;" +
                "fillColor=" + type.getFillColor() + ";" +
                "strokeColor=#666666;" +
                "fontColor=" + type.getFontColor() + ";" +
                "fontSize=12;" +
                "fontStyle=1;" +
                "shadow=1;";
    }

    private void addConnectionCell(Document doc, Element root, Connection connection) {
        Element cell = doc.createElement("mxCell");
        cell.setAttribute("id", connection.id());
        cell.setAttribute("value", connection.label() != null ? connection.label() : "");
        cell.setAttribute("style", "endArrow=classic;html=1;strokeColor=#333333;fontSize=10;");
        cell.setAttribute("edge", "1");
        cell.setAttribute("parent", "1");
        cell.setAttribute("source", connection.sourceId());
        cell.setAttribute("target", connection.targetId());

        Element geometry = doc.createElement("mxGeometry");
        geometry.setAttribute("relative", "1");
        geometry.setAttribute("as", "geometry");
        cell.appendChild(geometry);

        root.appendChild(cell);
    }

    private String toXmlString(Document doc) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }
}
