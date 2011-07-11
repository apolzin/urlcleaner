package apolzin.unshitify

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

import java.net.{URLEncoder, HttpURLConnection, URL}
import util.control.Breaks._
import com.sun.org.apache.xpath.internal.operations.Variable
import org.datanucleus.store.mapped.expression.ExpressionLogicSetAdapter

class Unshorten extends HttpServlet{
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    if(req.getParameter("url") != null){
      val head = getHead(escapeURL(req.getParameter("url")))
      if(head != null && !req.getParameter("url").matches(".*snipr.*")){
        if(req.getParameter("url").matches(".*t\\.co\\/.*")){
          val tco_head = getHead(head)
          if(tco_head != null){
            if(tco_head.matches("^http.*")){
              resp.getWriter.println(tco_head)
            } else {

              resp.getWriter.println(head)
            }
           } else {
            resp.getWriter.println(head)
          }
        } else {
          resp.getWriter.println(head)
        }
       } else {
        resp.getWriter.println(req.getParameter("url"))
      }
    } else {
      resp.getWriter.println("no url supplied")
    }
  }

  def getHead(url: String) = {
    try{
      val urlObj = new URL(url)
      val con = urlObj.openConnection.asInstanceOf[HttpURLConnection]
      con.setRequestMethod("HEAD")
      con.setDoOutput(true)
      con.setInstanceFollowRedirects(false)
      con.connect()
      //con.getHeaderField("Location")
      var i = 0
      var continue = true
      var location = ""
      while(continue){
        if(con.getHeaderFieldKey(i) == null && con.getHeaderField(i) == null ){
          continue = false
        } else {
          if(con.getHeaderFieldKey(i) == "location"){
            location += con.getHeaderField(i)

          }
          i += 1
        }
      }
      if(location == ""){
        null
      } else {
        location
      }
    } catch {
      case e => url
    }
  }

  def escapeURL(url: String) = {
      val reg = "[&?][^&^?^=]+=[^&^?^=]+".r
      reg.replaceAllIn(url, m => m.matched.split("=")(0) + "=" + URLEncoder.encode(m.matched.split("=")(1), "utf-8"))
  }
}