package com.example.accountservice.controller;

import com.example.accountservice.model.*;
import com.example.accountservice.response.JWTAuthResponse;
import com.example.accountservice.security.JwtTokenProvider;
import com.example.accountservice.service.AuthService;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Build Login REST API
    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JWTAuthResponse> login(@RequestBody LoginDto loginDto){
        String token = authService.login(loginDto);

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    // Build Register REST API
    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        String response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Api /api/random yêu cầu phải xác thực mới có thể request
    @GetMapping("/random")
    public String randomStuff(){
        return "JWT Hợp lệ mới có thể thấy được message này";
    }

    @RequestMapping(value = "/refreshtoken", method = RequestMethod.GET)
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) throws Exception {
        // From the HttpRequest get the claims
        DefaultClaims claims = (io.jsonwebtoken.impl.DefaultClaims) request.getAttribute("claims");

        Map<String, Object> expectedMap = getMapFromIoJsonwebtokenClaims(claims);
        String token = authService.refreshtoken(expectedMap, expectedMap.get("sub").toString());
        return ResponseEntity.ok(new JWTAuthResponse(token));
    }

    public Map<String, Object> getMapFromIoJsonwebtokenClaims(DefaultClaims claims) {
        Map<String, Object> expectedMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        return expectedMap;
    }

//	@GetMapping("/")
//	public String home(Principal principal){
//		Map<String, Object> authDetails = (Map<String, Object>) ((OAuth2Authentication) principal)
//				.getUserAuthentication()
//				.getDetails();
//
//		String userName = (String) authDetails.get("name");
//
//		return "Hey " + userName +", Welcome to ANDP1";
//	}
}