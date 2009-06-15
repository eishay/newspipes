package test.search

import junit.framework.TestCase
import scala.testing.SUnit.Assert
import com.newspipes.server.core.TwitterSearch

class TestSearch extends TestCase with Assert {

  def testNoLink : Unit = {
    val search = new TwitterSearch
    assertEq(None, search.extractUrlFromContent("nothing here"))
    assertEq(None, search.extractUrlFromContent(""))
  }

  def testWithLink : Unit = {
    val search = new TwitterSearch
    var result = search.extractUrlFromContent("""something a href="http://www.linkedin.com" end""")
    assertEquals("got " + result.toString, Some("http://www.linkedin.com"), result)
    result = search.extractUrlFromContent("""something a href="http://www.linkedin.com" a href="http://www.linkedin2.com" end""")
    assertEquals("got " + result.toString, Some("http://www.linkedin.com"), result)
  }

  def testXmlWithLink : Unit = {
    val search = new TwitterSearch
    val link = "http://www.site.com/something&param=aaa"
    val xml = <feed xmlns:google="http://base.google.com/ns/1.0" xml:lang="en-US" xmlns:openSearch="http://a9.com/-/spec/opensearch/1.1/" xmlns="http://www.w3.org/2005/Atom" xmlns:twitter="http://api.twitter.com/">
                <id>tag:search.twitter.com,2005:search/scala</id>
                <link type="text/html" rel="alternate" href="http://search.twitter.com/search?q=scala"/>
                <link type="application/atom+xml" rel="self" href="http://search.twitter.com/search.atom?q=scala"/>
                <title>scala - Twitter Search</title>
                <link type="application/opensearchdescription+xml" rel="search" href="http://search.twitter.com/opensearch.xml"/>
                <link type="application/atom+xml" rel="refresh" href="http://search.twitter.com/search.atom?q=scala&amp;since_id=2169047983"/>
                <twitter:warning>since_id removed for pagination.</twitter:warning>
                <updated>2009-06-14T20:52:36Z</updated>
                <openSearch:itemsPerPage>15</openSearch:itemsPerPage>
                <link type="application/atom+xml" rel="next" href="http://search.twitter.com/search.atom?max_id=2169047983&amp;page=2&amp;q=scala"/>
                <entry>
                  <id>tag:search.twitter.com,2005:2169047983</id>
                  <published>2009-06-14T20:52:36Z</published>
                  <link type="text/html" rel="alternate" href="http://twitter.com/danybus1/statuses/2169047983"/>
                  <title>Dal punto di vista etimologico, volgare significa solo popolare su scala di massa. E' l'umilt&#224; col riporto. (gi&#224; adoro David Foster Wallace)</title>
                  <content type="html">Dal punto di vista etimologico, volgare a href="http://www.site.com/something&amp;param=aaa" significa solo popolare su &lt;b&gt;scala&lt;/b&gt; di massa. E' l'umilt&#224; col riporto. (gi&#224; adoro David Foster Wallace)</content>
                  <updated>2009-06-14T20:52:36Z</updated>
                  <link type="image/png" rel="image" href="http://s3.amazonaws.com/twitter_production/profile_images/53734327/Foto_58_normal.jpg"/>
                  <twitter:source>&lt;a href="http://twitter.com/"&gt;web&lt;/a&gt;</twitter:source>
                  <twitter:lang>it</twitter:lang>
                  <author>
                    <name>danybus1 (Daniele)</name>
                    <uri>http://twitter.com/danybus1</uri>
                  </author>
                </entry>
                <entry>
                  <id>tag:search.twitter.com,2005:2169033022</id>
                  <published>2009-06-14T20:51:09Z</published>
                  <link type="text/html" rel="alternate" href="http://twitter.com/michaelg/statuses/2169033022"/>
                  <title>Inspiration for next phase of Scala-Android integration came to me while swinging at playground</title>
                  <content type="html">Inspiration for next phase of &lt;b&gt;Scala&lt;/b&gt;-Android integration came to me while swinging at playground</content>
                  <updated>2009-06-14T20:51:09Z</updated>
                  <link type="image/png" rel="image" href="http://s3.amazonaws.com/twitter_production/profile_images/74701718/2862073028_c5888db723_o_normal.jpg"/>
                  <twitter:source>&lt;a href="http://orangatame.com/products/twitterberry/"&gt;TwitterBerry&lt;/a&gt;</twitter:source>
                  <twitter:lang>en</twitter:lang>
                  <author>
                    <name>michaelg (Michael Galpin)</name>
                    <uri>http://twitter.com/michaelg</uri>
                  </author>
                 </entry>
                </feed>;
    val elements = xml \ "entry"
    assertEquals(2, elements.size)
    assertEquals(None, search.extractUrl(elements(0))(0))
    assertEquals(Some(link), search.extractUrl(elements(1))(0))
    var result = search.extractUrls(elements)
    assertEquals(1, result.size)
    assertEquals(link, result(0))
  }

}