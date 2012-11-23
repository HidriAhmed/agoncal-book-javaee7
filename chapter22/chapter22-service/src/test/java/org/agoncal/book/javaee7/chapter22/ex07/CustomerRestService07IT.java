package org.agoncal.book.javaee7.chapter22.ex07;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @author Antonio Goncalves
 *         APress Book - Beginning Java EE 7 with Glassfish 4
 *         http://www.apress.com/
 *         http://www.antoniogoncalves.org
 *         --
 */
public class CustomerRestService07IT {

  // ======================================
  // =             Attributes             =
  // ======================================

  private static HttpServer server;
  private static URI uri = UriBuilder.fromUri("http://localhost/").port(8282).build();
  private static Client client = ClientFactory.newClient();

  // ======================================
  // =          Lifecycle Methods         =
  // ======================================

  @BeforeClass
  public static void init() throws IOException {
    // create a new server listening at port 8080
    server = HttpServer.create(new InetSocketAddress(uri.getPort()), 0);

    // create a handler wrapping the JAX-RS application
    HttpHandler handler = RuntimeDelegate.getInstance().createEndpoint(new ApplicationConfig07(), HttpHandler.class);

    // map JAX-RS handler to the server root
    server.createContext(uri.getPath(), handler);

    // start the server
    server.start();
  }

  @AfterClass
  public static void stop() {
    server.stop(0);
  }

  // ======================================
  // =              Unit tests            =
  // ======================================

  @Test
  public void shouldMarshallACustomer() throws JAXBException {
    // given
    Customer07 customer = new Customer07("John", "Smith", "jsmith@gmail.com", "1234565", new Date(), new Date());
    StringWriter writer = new StringWriter();
    JAXBContext context = JAXBContext.newInstance(Customer07.class);
    Marshaller m = context.createMarshaller();
    m.marshal(customer, writer);
  }

  @Test
  public void shouldCheckGetCustomerByLoginURI() {
    Response response = client.target("http://localhost:8282/07/customer/agoncal").request().get();
    assertEquals(200, response.getStatus());
  }

  @Test
  public void shouldCheckGetCustomerByIdURI() {
    Response response = client.target("http://localhost:8282/07/customer/1234").request().get();
    assertEquals(200, response.getStatus());
  }

  @Test
  public void shouldCheckGetCustomerByZipCodeURI() {
    Response response = client.target("http://localhost:8282/07/customer?zip=75012").request().get();
    assertEquals(200, response.getStatus());
  }

  @Test
  public void shouldCheckGetCustomerByZipCodeWithParamURI() {
    Response response = client.target("http://localhost:8282/07/customer").queryParam("zip", 75011L).request().get();
    assertEquals(200, response.getStatus());
  }

  @Test
  public void shouldCheckGetCustomerByFirstnameNameURI() {
    Response response = client.target("http://localhost:8282/07/customer/search;firstname=Antonio;surname=Goncalves").request().get();
    assertEquals(200, response.getStatus());
  }

  @Test
  public void shouldCheckGetCustomerByFirstnameNameWithParamURI() {
    Response response = client.target("http://localhost:8282/07/customer/search").matrixParam("firstname","Antonio2").matrixParam("surname","Goncalves2").request().get();
    assertEquals(200, response.getStatus());
  }

    @Test
    public void shouldCheckGetCustomerWithCookieParamURI() {
        Cookie myCookie = new Cookie("myCookie", "This is my cookie");
        String response = client.target("http://localhost:8282/07/customer/cookie").request().cookie(myCookie).get(String.class);
        assertEquals("This is my cookie from the server", response);
    }

    @Test
    public void shouldEchoUserAgentValue() {
        String response = client.target("http://localhost:8282/07/customer/userAgent").request().get(String.class);
        assertEquals("Jersey/2.0-m09 (HttpUrlConnection 1.7.0_02) from the server", response);
    }

    @Test @Ignore
    public void shouldEchoUserAgentWithReponse() {
        Response response = client.target("http://localhost:8282/07/customer/userAgentRep").request().get();
        assertEquals(200, response.getStatus());
        assertEquals("Jersey/2.0-m09 (HttpUrlConnection 1.7.0_02) from the server", response.getEntity());
    }
}