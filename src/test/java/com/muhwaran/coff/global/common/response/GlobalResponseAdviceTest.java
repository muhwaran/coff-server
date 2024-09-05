package com.muhwaran.coff.global.common.response;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.MockHttpServletResponse;

class GlobalResponseAdviceTest {

	private GlobalResponseAdvice advice;

	@Mock
	private MethodParameter returnType;

	@Mock
	private ServerHttpRequest request;

	@Mock
	private ServletServerHttpResponse response;

	private MockHttpServletResponse servletResponse;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		advice = new GlobalResponseAdvice();
		servletResponse = new MockHttpServletResponse();
		when(response.getServletResponse()).thenReturn(servletResponse);
	}

	@Test
	void supports_shouldAlwaysReturnTrue() {
		assertTrue(advice.supports(returnType, null));
	}

	@Test
	void beforeBodyWrite_withSuccessStatus_shouldReturnGlobalResponse() {
		servletResponse.setStatus(HttpStatus.OK.value());

		Object result = advice.beforeBodyWrite(null, returnType, MediaType.APPLICATION_JSON,
			null, request, response);

		System.out.println("result = " + result);
		assertInstanceOf(GlobalResponse.class, result);
		GlobalResponse globalResponse = (GlobalResponse)result;
		assertEquals(HttpStatus.OK.value(), globalResponse.status());
	}

	@Test
	void beforeBodyWrite_withErrorStatus_shouldReturnOriginalBody() {
		Object body = "에러 메시지";
		servletResponse.setStatus(HttpStatus.BAD_REQUEST.value());

		Object result = advice.beforeBodyWrite(body, returnType, MediaType.APPLICATION_JSON,
			null, request, response);

		assertEquals(body, result);
	}

	@Test
	void beforeBodyWrite_withStringBody_shouldReturnOriginalBody() {
		String body = "문자열 본문";
		servletResponse.setStatus(HttpStatus.OK.value());

		Object result = advice.beforeBodyWrite(body, returnType, MediaType.APPLICATION_JSON,
			null, request, response);

		assertEquals(body, result);
	}

	@Test
	void beforeBodyWrite_withNullHttpStatus_shouldReturnOriginalBody() {
		Object body = "알 수 없는 상태";
		servletResponse.setStatus(999); // 존재하지 않는 HTTP 상태 코드

		Object result = advice.beforeBodyWrite(body, returnType, MediaType.APPLICATION_JSON,
			null, request, response);

		assertEquals(body, result);
	}
}
