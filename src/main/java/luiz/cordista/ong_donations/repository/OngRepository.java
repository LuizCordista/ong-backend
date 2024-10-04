package luiz.cordista.ong_donations.repository;

import luiz.cordista.ong_donations.model.Ong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OngRepository extends JpaRepository<Ong, String> {
    Ong findByEmail(String email);
}
