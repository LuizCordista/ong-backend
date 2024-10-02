package luiz.cordista.ong_donations.service;

import luiz.cordista.ong_donations.model.Ong;
import luiz.cordista.ong_donations.model.OngDetails;
import luiz.cordista.ong_donations.repository.OngRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OngDetailsService implements UserDetailsService {

    private final OngRepository ongRepository;

    public OngDetailsService(OngRepository ongRepository) {
        this.ongRepository = ongRepository;
    }

    @Override
    public OngDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Ong ong = ongRepository.findByEmail(username);
        return new OngDetails(ong);
    }
}
