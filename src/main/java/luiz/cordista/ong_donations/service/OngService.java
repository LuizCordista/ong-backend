package luiz.cordista.ong_donations.service;

import luiz.cordista.ong_donations.dto.OngDataDTO;
import luiz.cordista.ong_donations.dto.RegisterRequestDTO;
import luiz.cordista.ong_donations.model.Ong;
import luiz.cordista.ong_donations.model.OngDetails;
import luiz.cordista.ong_donations.repository.OngRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OngService {

    private final OngRepository ongRepository;
    private final PasswordEncoder passwordEncoder;

    public OngService(OngRepository ongRepository, PasswordEncoder passwordEncoder) {
        this.ongRepository = ongRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Ong createOng(RegisterRequestDTO registerRequestDTO) {
        if (registerRequestDTO == null) {
            throw new IllegalArgumentException("Ong cannot be null");
        }
        String encryptedPassword = passwordEncoder.encode(registerRequestDTO.password());
        Ong ong = new Ong(registerRequestDTO.name(), registerRequestDTO.email(), encryptedPassword);
        return ongRepository.save(ong);
    }

    public Ong getOngByEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return ongRepository.findByEmail(email);
    }

    public OngDataDTO getOngDetails(Ong ong) {
        if (ong == null) {
            throw new IllegalArgumentException("Ong cannot be null");
        }
        return new OngDataDTO(ong.getId(), ong.getName(), ong.getEmail());
    }

    public Optional<Ong> getAuthenticatedCustomer() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return Optional.ofNullable(ongRepository.findByEmail(email));
    }
}
