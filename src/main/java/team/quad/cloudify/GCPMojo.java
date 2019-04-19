package team.quad.cloudify;

import jline.console.ConsoleReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

import static org.fusesource.jansi.Ansi.ansi;

@Mojo(name = "gcp")
public class GCPMojo extends AbstractMojo {

  @Component
  private MavenProject project;

  public void execute() throws MojoExecutionException {
    File projectRoot = new File(".");
    Map<String, String> context = Collections.singletonMap("artifactId", project.getArtifactId());
    applyReplacements("gcp/cloudbuild.yaml", new File(projectRoot, "cloudbuild.yaml"), context);
    applyReplacements("gcp/deploy", new File(projectRoot, "deploy"), context);
    applyReplacements("gcp/deploy.cmd", new File(projectRoot, "deploy.cmd"), context);
    applyReplacements("gcp/Dockerfile", new File(projectRoot, "Dockerfile"), context);

    printMessage();
  }

  private void applyReplacements(String resource, File target, Map<String, String> context) throws MojoExecutionException {
    try {
      ClassLoader classLoader = getClass().getClassLoader();
      InputStream resourceInputStream = classLoader.getResourceAsStream(resource);

      if (resourceInputStream == null) {
        throw new IllegalArgumentException("Resource " + resource + " not found");
      }

      String utf8 = IOUtils.toString(resourceInputStream, Charset.forName("UTF8"));
      String artifactId = StrSubstitutor.replace(utf8, context);
      FileUtils.writeStringToFile(target, artifactId, Charset.forName("UTF8"));
    } catch (IOException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  private void printMessage() {
    getLog().info("");
    getLog().info("========================================================================");
    getLog().info(ansi().a("Google Cloud Run has been enabled").toString());
    getLog().info("========================================================================");
    getLog().info("");
  }
}
