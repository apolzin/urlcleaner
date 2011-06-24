package apolzin.unshitify

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

import java.net.{URLEncoder, HttpURLConnection, URL}

class Unshorten extends HttpServlet{
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    if(req.getParameter("url") != null){
      val head = getHead(escapeURL(req.getParameter("url")))
      if(head != null && !req.getParameter("url").matches(".*snipr.*")){
        resp.getWriter.println(head)
      } else {
        resp.getWriter.println(req.getParameter("url"))
      }
    } else {
      resp.getWriter.println("no url supplied")
    }
  }

  def getHead(url: String) = {
    val urlObj = new URL(url)
    val con = urlObj.openConnection.asInstanceOf[HttpURLConnection]
    con.setRequestMethod("HEAD")
    con.setDoOutput(true)
    con.setInstanceFollowRedirects(false)
    con.connect()
    con.getHeaderField("Location")
  }

  def escapeURL(url: String) = {
      val reg = "[&?][^&^?^=]+=[^&^?^=]+".r
      reg.replaceAllIn(url, m => m.matched.split("=")(0) + "=" + URLEncoder.encode(m.matched.split("=")(1), "utf-8"))
  }
}