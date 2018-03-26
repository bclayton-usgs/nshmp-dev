package gov.usgs.earthquake.nshmp.postgres.sources;

import static gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute.*;

import java.io.File;
import java.io.IOException;

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

import gov.usgs.earthquake.nshmp.eq.fault.surface.RuptureScaling;
import gov.usgs.earthquake.nshmp.postgres.source_settings.DefaultMfd;
import gov.usgs.earthquake.nshmp.postgres.source_settings.DeformationModel;
import gov.usgs.earthquake.nshmp.postgres.source_settings.MagUncertainty;

/**
 * Export a {@link FaultSourceSet} to a XML file.
 * 
 * @author Brandon Clayton
 * @see XMLExporter#writeXML(ModelParameters, String)
 */
public class FaultXMLExporter {
  static final String DISCLAIMER = 
      " This model is an example and for review purposes only ";
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
   *            <SourceProperties [attributes]>
   *              <RuptureScalingModels>
   *                <Model id="" weight="" />
   *                <Model id="" weight="" />
   *              </RuptureScalingModels>
   *            </SourceProperties>
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
  public static void writeXML (
      FaultSourceSet sourceSet, 
      String outputDir, 
      String fileOuput) 
      throws TransformerException, ParserConfigurationException, IOException {
    String xmlOutputDir = MAIN_OUTPUT_DIR + "/" + outputDir + "/";
    checkDirectory(xmlOutputDir);
    
    /* Create document */
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    Document doc = docBuilder.newDocument();
    doc.setXmlStandalone(true);
    
    /* Create fault source set (root) XML node */
    Element rootEl = doc.createElement(sourceSet.type.toUpperCamelCase());
    for (SourceAttribute key : sourceSet.attributes.keySet()) {
      rootEl.setAttribute(key.toLowerCamelCase(), sourceSet.attributes.get(key));
    }
    
    Comment disclaimer = doc.createComment(DISCLAIMER);
    rootEl.appendChild(disclaimer);
    doc.appendChild(rootEl);
    
    /* Create settings node */
    Element settingsEl = doc.createElement(SETTINGS.toUpperCamelCase());
  
    /* Create all incremental mfds */
    Element defaultMfdsEl = doc.createElement(DEFAULT_MFDS.toUpperCamelCase());
    
    for (DefaultMfd mfd : sourceSet.defaultMfds.mfds) {
      Element incrementalMfdEl = doc.createElement(INCREMENTAL_MFD.toUpperCamelCase());  
      for (SourceAttribute key : mfd.attributes.keySet()) {
        incrementalMfdEl.setAttribute(key.toLowerCamelCase(), mfd.attributes.get(key));
      }
      defaultMfdsEl.appendChild(incrementalMfdEl);
    }
    settingsEl.appendChild(defaultMfdsEl);
    
    /* Create all mag uncertainties */
    Element magUncertaintyEl = doc.createElement(MAG_UNCERTAINTY.toUpperCamelCase());
    for (MagUncertainty mag : sourceSet.magUncertainties.sigmas) {
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
    
    /* Create rupture scaling models node */
    Element ruptureEl = doc.createElement(RUPTURE_SCALING_MODELS.toUpperCamelCase());
    for (RuptureScaling model : sourceSet.ruptureScalingModels.models) {
      Element modelEl = doc.createElement("Model");
      modelEl.setAttribute(ID.toLowerCase(), model.toString());
      ruptureEl.appendChild(modelEl);
    }
    sourcePropEl.appendChild(ruptureEl);
    settingsEl.appendChild(sourcePropEl);
    
    /* Append all settings */
    rootEl.appendChild(settingsEl);
    
    /* Create all source nodes */
    for (FaultSource source : sourceSet.sources) {
      Element sourceEl = doc.createElement(SOURCE.toUpperCamelCase());
      for (SourceAttribute key : source.attributes.keySet()) {
        sourceEl.setAttribute(key.toLowerCamelCase(), source.attributes.get(key));
      }
      rootEl.appendChild(sourceEl);
      
      /* Create all deformation model nodes */
      for (DeformationModel defModel : source.deformationModels.models) {
        Element rateEl = doc.createElement(DEFORMATION_MODEL.toUpperCamelCase());
        rateEl.setAttribute(ID.toLowerCase(), defModel.id);
        rateEl.setAttribute(RATE.toLowerCase(), defModel.rate);
        sourceEl.appendChild(rateEl);
      }
      
      /* Create geometry node */
      Element geometryEl = doc.createElement(GEOMETRY.toUpperCamelCase());
      for (SourceAttribute key : source.geometry.attributes.keySet()) {
        geometryEl.setAttribute(
            key.toLowerCamelCase(), 
            source.geometry.attributes.get(key));
      }
      
      /* Create fault trace node */
      Element faultTraceEl = doc.createElement(TRACE.toUpperCamelCase());
      faultTraceEl.appendChild(doc.createTextNode(source.geometry.trace.toString()));
      geometryEl.appendChild(faultTraceEl);
      sourceEl.appendChild(geometryEl);
    }
    
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource domSource = new DOMSource(doc);
    StreamResult streamResult = new StreamResult(xmlOutputDir + 
        fileOuput + ".xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    transformer.transform(domSource, streamResult);
  }
  
  /*
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