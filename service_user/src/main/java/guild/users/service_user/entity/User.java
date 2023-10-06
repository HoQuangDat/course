package guild.users.service_user.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(name = "userName", length = 25)
    @Nullable
    private String userName;

    @Column(name = "password", length = 25)
    @Nullable
    private String password;

    @Column(name = "fullName", length = 25)
    @Nullable
    private String fullName;

    @Column(name = "phone", length = 13)
    @Nullable
    private String phone;

    @Column(name = "email", length = 35)
    @Nullable
    private String email;
}
