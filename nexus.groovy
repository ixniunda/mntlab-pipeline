// modules
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7' )
import com.sun.xml.internal.ws.api.pipe.ContentType
import javax.xml.ws.spi.http.HttpContext
import groovyx.net.http.*
import org.apache.http.*
import groovyx.net.http.HTTPBuilder

static void main(String[] args) {

    def artifact = System.getProperty('ArtifactID')
    def group = System.getProperty('GroupID')
    def number = System.getProperty('VersionID')
    def action = System.getProperty('Action')
    def src = System.getProperty('Src')

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
    
    // upload function
    def nexussrv = "http://epbyminw1969:8081/repository"
    def repo = "maven-releases"
    def studentName = "kzalialetdzinau"

            URL url = new URL("${nexussrv}/${repo}/pipeline-${studentName}-${number}"+".tar.gz")
            def authString = "nexus-service-user:deploy".getBytes().encodeBase64().toString()
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection()
            httpCon.setDoOutput(true)
            httpCon.setRequestMethod("PUT")
            httpCon.setRequestProperty( "Authorization", "Basic ${authString}" )
            def file = new File("pipeline-${studentName}-${number}.tar.gz").bytes
            def out = new DataOutputStream(httpCon.outputStream)
            out.write(file)
            out.flush()
            out.close()
            println(httpCon.getResponseCode())
}