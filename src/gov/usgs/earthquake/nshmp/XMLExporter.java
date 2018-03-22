package gov.usgs.earthquake.nshmp;

import static gov.usgs.earthquake.nshmp.SourceAttribute.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.usgs.earthquake.nshmp.internal.UsRegion;

/**
 * Export {@link ModelParameters} to a XML file.
 * 
 * @author Brandon Clayton
 * @see XMLExporter#writeXML(ModelParameters, String)
 */
public class XMLExporter {
  static final String DISCLAIMER = 
      " This model is an example and for review purposes only ";
  static final String FAULT_SOURCE_ID = "-1";
  static final String FAULT_SOURCE_WEIGHT = "1.0";
  static final String MAIN_OUTPUT_DIR = "output";

  /**
   * Write a XML file with following outline: 
   *    <pre> 
   *      {@code 
   *        <FaultSourceSet name="">
   *          <Settings>
   *            <DefaultMfds>
   *              <IncrementalMfd type="" ... />
   *            </DefaultMfds>
   *            <MagUncertainty>
   *              <Epistemic ... />
   *              <Aleatory ... />
   *            </MagUncertainty>
   *            <SourceProperties ruptureScaling="" />
   *          </Settings>
   *          <Source name="Some fault zone">
   *            <DeformationModel id="BIRD" rate=""/>
   *            <DeformationModel id="GEO" rate=""/>
   *            <DeformationModel id="ZENG" rate=""/>
   *            <Geometry depth="" dip="" rake="" width="">
   *              <Trace>
   *              </Trace>
   *            </Geometry>
   *          </Source>
   *        </FaultSourceSet>
   *      }
   *    </pre>
   * @param model
   * @throws TransformerException
   * @throws ParserConfigurationException
   * @throws IOException 
   */
  static void writeXML (ModelParameters model, String outputDir) 
      throws TransformerException, ParserConfigurationException, IOException {
    String xmlOutputDir = MAIN_OUTPUT_DIR + "/" + outputDir + "/";
    checkDirectory(xmlOutputDir);
    
    UsRegion primaryState = UsRegion.valueOf(model.stateAbbrev);
    
    /* Create document */
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    Document doc = docBuilder.newDocument();
    doc.setXmlStandalone(true);
    
    /* Create fault source set (root) XML node */
    Element rootEl = doc.createElement(FAULT_SOURCE_SET.toUpperCamelCase());
    rootEl.setAttribute(
        NAME.toLowerCase(),
        primaryState + " " + FAULTS.toUpperCamelCase());
    rootEl.setAttribute(ID.toLowerCase(), FAULT_SOURCE_ID);
    rootEl.setAttribute(WEIGHT.toLowerCase(), FAULT_SOURCE_WEIGHT);
    Comment disclaimer = doc.createComment(DISCLAIMER);
    rootEl.appendChild(disclaimer);
    doc.appendChild(rootEl);
    
    /* Create settings node */
    Element settingsEl = doc.createElement(SETTINGS.toUpperCamelCase());
    
    /* Create all incremental mfds */
    Element defaultMfdsEl = doc.createElement(DEFAULT_MFDS.toUpperCamelCase());
    for (DefaultMfd mfd : model.defaultMfds) {
      Element incrementalMfdEl = doc.createElement(INCREMENTAL_MFD.toUpperCamelCase());  
      for (SourceAttribute key : mfd.attributes.keySet()) {
        incrementalMfdEl.setAttribute(key.toLowerCamelCase(), mfd.attributes.get(key));
      }
      defaultMfdsEl.appendChild(incrementalMfdEl);
    }
    settingsEl.appendChild(defaultMfdsEl);
    
    /* Create all mag uncertainties */
    Element magUncertaintyEl = doc.createElement(MAG_UNCERTAINTY.toUpperCamelCase());
    for (MagUncertainty mag : model.magUncertainties) {
      Element magEl = doc.createElement(mag.attributes.get(TYPE));
      for (SourceAttribute key : mag.attributes.keySet()) {
        if (key.equals(TYPE)) continue;
        magEl.setAttribute(key.toLowerCamelCase(), mag.attributes.get(key));
      }
      magUncertaintyEl.appendChild(magEl);
    }
    settingsEl.appendChild(magUncertaintyEl);
    
    /* Create source properties */
    Element sourcePropEl = doc.createElement(SOURCE_PROPERTIES.toUpperCamelCase());
    sourcePropEl.setAttribute(RUPTURE_SCALING.toLowerCamelCase(), 
        model.ruptureScaling.toString());
    settingsEl.appendChild(sourcePropEl);
    
    /* Append all settings */
    rootEl.appendChild(settingsEl);
    
    /* Create all source nodes */
    for (int jf = 0; jf < model.name.size(); jf++) {
      Element sourceEl = doc.createElement(SOURCE.toUpperCamelCase());
      sourceEl.setAttribute(NAME.toLowerCase(), model.name.get(jf));
      sourceEl.setAttribute(ID.toLowerCase(),"?");
      rootEl.appendChild(sourceEl);
      
      /* Create all deformation model nodes */
      Map<SourceAttribute, String> deformationModel = new HashMap<>();
      deformationModel.put(BIRD, model.birdRate.get(jf).toString());
      deformationModel.put(GEO, model.geoRate.get(jf).toString());
      deformationModel.put(ZENG, model.zengRate.get(jf).toString());
      
      for (SourceAttribute rateModel : deformationModel.keySet()) {
        Element rateEl = doc.createElement(DEFORMATION_MODEL.toUpperCamelCase());
        rateEl.setAttribute(ID.toLowerCase(), rateModel.toUpperCase());
        rateEl.setAttribute(RATE.toLowerCase(), deformationModel.get(rateModel));
        sourceEl.appendChild(rateEl);
      }
      
      /* Create geometry node */
      Element geometryEl = doc.createElement(GEOMETRY.toUpperCamelCase());
      geometryEl.setAttribute(DEPTH.toLowerCase(), model.depth.get(jf).toString());
      geometryEl.setAttribute(DIP.toLowerCase(), model.dip.get(jf).toString());
      geometryEl.setAttribute(RAKE.toLowerCase(), model.rake.get(jf).toString());
      geometryEl.setAttribute(WIDTH.toLowerCase(), model.width.get(jf).toString());
      
      /* Create fault trace node */
      Element faultTraceEl = doc.createElement(TRACE.toUpperCamelCase());
      faultTraceEl.appendChild(doc.createTextNode(model.faultTrace.get(jf)));
      geometryEl.appendChild(faultTraceEl);
      sourceEl.appendChild(geometryEl);
    }
    
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource domSource = new DOMSource(doc);
    StreamResult streamResult = new StreamResult(xmlOutputDir + 
        primaryState + ".xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    transformer.transform(domSource, streamResult);
  }
  
  /**
   * Create the output directory for the XML files.
   * @param xmlOutputDir
   * @throws IOException
   */
  private static void checkDirectory (String xmlOutputDir) throws IOException {
    File outputDir = new File(xmlOutputDir);
    
    if (!outputDir.exists()) {
      Boolean createdDir = outputDir.mkdirs();
      if (!createdDir) {
        throw new IOException(xmlOutputDir + " not created.");
      }
    }
  }
  
}