package com.userbot.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userbot.test.dto.DTOCity;
import com.userbot.test.dto.DTOInfoDetail;
import com.userbot.test.dto.DTORegion;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RestTests {

	private final static String COUNTRY_TEST = "COUNTRY_TEST";
	private final static String REGION_TEST = "REGION_TEST";
	private final static String CITY_TEST = "CITY_TEST";

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private MockMvc mockMvc;

	@PostConstruct
	public Boolean disableSSLValidation() throws Exception {
		final SSLContext sslContext = SSLContext.getInstance("TLS");

		sslContext.init(null, new TrustManager[] { new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		} }, null);

		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		hostNameVerifier();

		return true;
	}

	public void hostNameVerifier() {
		final HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier ();
		final HostnameVerifier localhostAcceptedHostnameVerifier = new HostnameVerifier () {
			public boolean verify ( String hostname, SSLSession sslSession ) {
				if ( hostname.equals ( "localhost" ) ) {
					return true;
				}
				return defaultHostnameVerifier.verify ( hostname, sslSession );
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier ( localhostAcceptedHostnameVerifier );
	}

	@Test
	void contextLoads() throws Exception {
		//Test Country
		DTOInfoDetail country = this.restTemplate
				.getForObject(restTemplate.getRootUri() + "/api/v1/countries/" + COUNTRY_TEST, DTOInfoDetail.class);

		if (country == null || country.getName() == null) {
			country = new DTOInfoDetail(COUNTRY_TEST, "Descrizione test");

			this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/countries")
							.contentType(MediaType.APPLICATION_JSON)
							.content(RestTests.asJsonString(country))
							.accept(MediaType.APPLICATION_JSON))
					.andDo(MockMvcResultHandlers.print())
					.andExpect(MockMvcResultMatchers.status().isCreated())
					.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(COUNTRY_TEST));

			country = this.restTemplate
					.getForObject(restTemplate.getRootUri() + "/api/v1/countries/" + COUNTRY_TEST, DTOInfoDetail.class);
		}

		Assertions.assertThat(country).isNotNull();

		country.setDescription("prova2");

		mockMvc.perform( MockMvcRequestBuilders
				.put("/api/v1/countries/{name}", COUNTRY_TEST)
				.content(RestTests.asJsonString(country))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.description").value("prova2"));

		DTOInfoDetail[] allCountry = this.restTemplate
				.getForObject(restTemplate.getRootUri() + "/api/v1/countries?page=0&size=10", DTOInfoDetail[].class);

		Assertions.assertThat(allCountry).isNotNull();
		Assertions.assertThat(allCountry.length).isGreaterThan(0);

		//Test Regions
		DTORegion region = this.restTemplate
				.getForObject(restTemplate.getRootUri() + "/api/v1/regions/" + REGION_TEST + "/" + COUNTRY_TEST, DTORegion.class);

		if (region == null || region.getName() == null) {
			DTOInfoDetail regionID = new DTOInfoDetail(REGION_TEST, "Descrizione test");

			this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/regions?countryName=" + COUNTRY_TEST)
							.contentType(MediaType.APPLICATION_JSON)
							.content(RestTests.asJsonString(regionID))
							.accept(MediaType.APPLICATION_JSON))
					.andDo(MockMvcResultHandlers.print())
					.andExpect(MockMvcResultMatchers.status().isCreated())
					.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(REGION_TEST));

			region = this.restTemplate
					.getForObject(restTemplate.getRootUri() + "/api/v1/regions/" + REGION_TEST + "/" + COUNTRY_TEST, DTORegion.class);
		}

		Assertions.assertThat(region).isNotNull();
		Assertions.assertThat(region.getId()).isNotNull();

		region = this.restTemplate
				.getForObject(restTemplate.getRootUri() + "/api/v1/regions/" + region.getId(), DTORegion.class);

		Assertions.assertThat(region).isNotNull();
		Assertions.assertThat(region.getId()).isNotNull();

		mockMvc.perform( MockMvcRequestBuilders
						.put("/api/v1/regions/{id}?countryName=" + COUNTRY_TEST, region.getId())
						.content(RestTests.asJsonString(new DTOInfoDetail(REGION_TEST, "prova2")))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.description").value("prova2"));

		DTORegion[] allRegions = this.restTemplate
				.getForObject(restTemplate.getRootUri() + "/api/v1/regions?page=0&size=10&countryName=" + COUNTRY_TEST, DTORegion[].class);

		Assertions.assertThat(allRegions).isNotNull();
		Assertions.assertThat(allRegions.length).isGreaterThan(0);

		//Test Cities
		DTOCity city = this.restTemplate
				.getForObject(restTemplate.getRootUri() + "/api/v1/cities/" + CITY_TEST + "/" + region.getId(), DTOCity.class);

		if (city == null || city.getName() == null) {
			DTOInfoDetail cityID = new DTOInfoDetail(CITY_TEST, "Descrizione test");

			this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cities?regionId=" + region.getId())
							.contentType(MediaType.APPLICATION_JSON)
							.content(RestTests.asJsonString(cityID))
							.accept(MediaType.APPLICATION_JSON))
					.andDo(MockMvcResultHandlers.print())
					.andExpect(MockMvcResultMatchers.status().isCreated())
					.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(CITY_TEST));

			city = this.restTemplate
					.getForObject(restTemplate.getRootUri() + "/api/v1/cities/" + CITY_TEST + "/" + region.getId(), DTOCity.class);
		}

		Assertions.assertThat(city).isNotNull();
		Assertions.assertThat(city.getId()).isNotNull();

		city = this.restTemplate
				.getForObject(restTemplate.getRootUri() + "/api/v1/cities/" + city.getId(), DTOCity.class);

		Assertions.assertThat(city).isNotNull();
		Assertions.assertThat(city.getId()).isNotNull();

		mockMvc.perform( MockMvcRequestBuilders
						.put("/api/v1/cities/{id}?regionId=" + region.getId(), city.getId())
						.content(RestTests.asJsonString(new DTOInfoDetail(CITY_TEST, "prova2")))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.description").value("prova2"));

		DTORegion[] allCities = this.restTemplate
				.getForObject(restTemplate.getRootUri() + "/api/v1/cities?page=0&size=10&regionId=" + region.getId(), DTORegion[].class);

		Assertions.assertThat(allCities).isNotNull();
		Assertions.assertThat(allCities.length).isGreaterThan(0);

		//Cancello tutto
		mockMvc.perform( MockMvcRequestBuilders.delete("/api/v1/countries/{name}", COUNTRY_TEST))
				.andExpect(MockMvcResultMatchers.status().isAccepted());

		mockMvc.perform( MockMvcRequestBuilders.delete("/api/v1/regions/{id}", region.getId()))
				.andExpect(MockMvcResultMatchers.status().isNotFound());

		mockMvc.perform( MockMvcRequestBuilders.delete("/api/v1/cities/{id}", city.getId()))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
