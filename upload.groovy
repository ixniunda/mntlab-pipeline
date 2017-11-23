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
    

    //def name1 = "group1/home-task/${number}/home-task-${number}.tar.gz"
    def nexussrv = "http://epbyminw1766.minsk.epam.com:8081/repository"
    def repo = "maven-releases"



            URL url = new URL("${nexussrv}/${repo}/pipeline-amakhnach-${number}"+".tar.gz")
            def authString = "admin:admin123".getBytes().encodeBase64().toString()
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection()
            httpCon.setDoOutput(true)
            httpCon.setRequestMethod("PUT")
            httpCon.setRequestProperty( "Authorization", "Basic ${authString}" )
            def file = new File("pipeline-amakhnach-${number}.tar.gz").bytes
            def out = new DataOutputStream(httpCon.outputStream)
            out.write(file)
            out.flush()
            out.close()
            println(httpCon.getResponseCode())


            break

}

