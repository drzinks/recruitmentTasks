import com.drzinks.Application;
import com.drzinks.dto.RepositoryDetailsDTO;
import com.drzinks.exception.GitHubApiException;
import com.drzinks.exception.RestTemplateResponseErrorHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {Application.class})
@RestClientTest
public class RepositoryDetailsServiceTest {

    public static final String USER_AND_REPO = "/username/reponame";
    @Autowired
    public RestTemplateBuilder builder;
    private RestTemplate restTemplate;
    private MockRestServiceServer server;

    @Before
    public void setup() {
        restTemplate = builder.errorHandler(new RestTemplateResponseErrorHandler().setPath(USER_AND_REPO)).build();
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test()
    public void testNotFound() {
        server.expect(ExpectedCount.once(), requestTo(USER_AND_REPO))
              .andExpect(method(HttpMethod.GET))
              .andRespond(withStatus(HttpStatus.NOT_FOUND));

        try {
            RepositoryDetailsDTO response = restTemplate.getForObject(USER_AND_REPO, RepositoryDetailsDTO.class);
        } catch (RestClientException e) {
            Assert.assertTrue(e.getCause() instanceof GitHubApiException);
            GitHubApiException gitHubApiException = (GitHubApiException) e.getCause();
            Assert.assertEquals(gitHubApiException.getApiError().getError(), "No such user/repo");
            Assert.assertEquals(gitHubApiException.getApiError().getMessage(), "No message available");
            Assert.assertEquals(gitHubApiException.getApiError().getPath(), "/repositories"+USER_AND_REPO);
            Assert.assertEquals(gitHubApiException.getApiError().getStatus(), HttpStatus.NOT_FOUND.value());
        }
        server.verify();

    }

    @Test()
    public void testInternalServerError() {

        server.expect(ExpectedCount.once(), requestTo(USER_AND_REPO))
              .andExpect(method(HttpMethod.GET))
              .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        try {
            RepositoryDetailsDTO response = restTemplate.getForObject(USER_AND_REPO, RepositoryDetailsDTO.class);
        } catch (RestClientException e) {
            Assert.assertTrue(e.getCause() instanceof GitHubApiException);
            GitHubApiException gitHubApiException = (GitHubApiException) e.getCause();
            Assert.assertEquals(gitHubApiException.getApiError().getError(), "GitHub Api does not work :(");
            Assert.assertEquals(gitHubApiException.getApiError().getMessage(), "No message available");
            Assert.assertEquals(gitHubApiException.getApiError().getPath(), "/repositories"+USER_AND_REPO);
            Assert.assertEquals(gitHubApiException.getApiError().getStatus(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        server.verify();
    }

    @Test()
    public void testFoundCase() {
        String mockedResponse = "{\n" +
                "    \"full_name\": \"drzinks/PhoneBook\",\n" +
                "    \"description\": \"Simple phone book web application basing on Spring MVC, Spring Data and MySQL\",\n" +
                "    \"clone_url\": \"https://github.com/drzinks/PhoneBook.git\",\n" +
                "    \"stargazers_count\": 0,\n" +
                "    \"created_at\": \"2016-09-05T09:24:52.000+0000\"\n" +
                "}";

        server.expect(ExpectedCount.once(), requestTo(USER_AND_REPO))
              .andExpect(method(HttpMethod.GET))
              .andRespond(withSuccess(mockedResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<RepositoryDetailsDTO> responseEntity = restTemplate.getForEntity(USER_AND_REPO, RepositoryDetailsDTO.class);
        RepositoryDetailsDTO response = responseEntity.getBody();
        Assert.assertEquals(response.getFullName(), "drzinks/PhoneBook");
        Assert.assertEquals(response.getDescription(), "Simple phone book web application basing on Spring MVC, Spring Data and MySQL");
        Assert.assertEquals(response.getCloneUrl(), "https://github.com/drzinks/PhoneBook.git");
        Assert.assertEquals(response.getStars(), 0);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Assert.assertEquals(dateFormat.format(response.getCreatedAt()), "2016-09-05T09:24:52.000+0000");
        server.verify();
    }
}
