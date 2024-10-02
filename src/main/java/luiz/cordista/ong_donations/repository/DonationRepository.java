package luiz.cordista.ong_donations.repository;

import luiz.cordista.ong_donations.model.Donation;
import luiz.cordista.ong_donations.model.Ong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, String> {
    List<Donation> findByOng(Ong ong);

}
