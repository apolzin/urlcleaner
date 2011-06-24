package apolzin.unshitify

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import vo.DomainKey
import util.control.Breaks._
import java.net.URLEncoder
import java.io.BufferedReader


class Unshitify extends HttpServlet {
    override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {

    resp.setContentType("text/plain")

    if(req.getParameter("url") != null){
      if(req.getParameter("url").matches(".*[&?].*")){
        val pm = PMF.get.getPersistenceManager

        var decodedURL = req.getQueryString.replace("url=","")

        var domainKey = new DomainKey(decodedURL)
        val key = domainKey.createKey

        val originalHeader = header(escapeURL(decodedURL))

        try{
          domainKey = pm.getObjectById(classOf[DomainKey], key)
        } catch {
          case e => ""
        }

        breakable {
          while(decodedURL.matches(".*[?&].*")){
            val opts = extractLastQueryPair(decodedURL)
            decodedURL = decodedURL.replaceFirst("[&?][^&^?^=]+=[^&^?^=]+$","")
            if(!domainKey.hasBannedKey(opts(0)) && !opts(0).matches("utm_.*")){
              var testHeader = header(escapeURL(decodedURL))
              if(testHeader == originalHeader){
                domainKey.addToBannedKeys(opts(0))
              } else {
                break()
              }
            }
          }
        }
        pm.makePersistent(domainKey)
        pm.close()
        resp.getWriter.println(decodedURL)
      } else{
        resp.getWriter.println(req.getParameter("url"))
      }
    }
    else {
      resp.getWriter.println("no url supplied")
    }

    def extractLastQueryPair(url: String) = {
      val reg = ".*[&?]([^&^?^=]+=[^&^?^=]+)$".r
      val reg(opts) = url
      opts.split("=")
    }

    def escapeURL(url: String) = {
      val reg = "[&?][^&^?^=]+=[^&^?^=]+".r
      reg.replaceAllIn(url, m => m.matched.split("=")(0) + "=" + URLEncoder.encode(m.matched.split("=")(1), "utf-8"))
    }

    def header(url: String) = {
      val source = io.Source.fromURL(url, "utf-8").reader()
      val buff = new BufferedReader(source)
      var ret = ""
      var out = ""
      breakable{
        while({out = buff.readLine(); out != null}) {
          ret += out
          if(out.matches("<body")){
            break()
          }
        }
      }
      ret
    }
  }

}