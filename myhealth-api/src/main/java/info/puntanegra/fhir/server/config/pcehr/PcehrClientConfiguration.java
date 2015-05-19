package info.puntanegra.fhir.server.config.pcehr;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.thetransactioncompany.cors.CORSFilter;

@Configuration
@ImportResource("classpath*:/META-INF/spring/app-context.xml")
public class PcehrClientConfiguration {
	
	@Bean
	public CORSFilter apiCorsFilter()
	{
	    return new CORSFilter();
	}
}
