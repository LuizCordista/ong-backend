package luiz.cordista.ong_donations.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import luiz.cordista.ong_donations.enums.DonationType;
import luiz.cordista.ong_donations.enums.Status;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String donorName;

    private DonationType donationType;

    private float amount;

    private String itemDescription;

    private Status status;


    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @ManyToOne
    private Ong ong;
}
