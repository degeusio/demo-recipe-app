import com.intuit.karate.KarateOptions;
import com.intuit.karate.junit4.Karate;
import org.junit.runner.RunWith;

/**
 * E.g. run with command
 * <pre><code>
 *  mvn test -Dtest=KarateRunner -Dkarate.env=develop -Denv_route_url=http://localhost:8080
 * </code></pre>
 */
@RunWith(Karate.class)
@KarateOptions(tags = "~@ignore")
public final class KarateRunner {

}
