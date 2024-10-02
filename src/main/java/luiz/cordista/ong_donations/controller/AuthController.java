package luiz.cordista.ong_donations.controller;

import luiz.cordista.ong_donations.auth.JwtTokenUtil;
import luiz.cordista.ong_donations.dto.LoginRequestDTO;
import luiz.cordista.ong_donations.dto.LoginResponseDTO;
import luiz.cordista.ong_donations.dto.RegisterRequestDTO;
import luiz.cordista.ong_donations.model.Ong;
import luiz.cordista.ong_donations.model.OngDetails;
import luiz.cordista.ong_donations.service.OngService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private OngService ongService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            Ong ong = ongService.getOngByEmail(loginRequestDTO.username());
            if (ong == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.username(), loginRequestDTO.password())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            OngDetails ongDetails = (OngDetails) authentication.getPrincipal();

            String jwt = jwtTokenUtil.generateToken(authentication);
            return ResponseEntity.ok(new LoginResponseDTO(jwt));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO ong) {
        try {
            if (ongService.getOngByEmail(ong.email()) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email já cadastrado");
            }
            ongService.createOng(ong);
            return ResponseEntity.ok("Ong cadastrada com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao cadastrar ong");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("logout");
    }


}
