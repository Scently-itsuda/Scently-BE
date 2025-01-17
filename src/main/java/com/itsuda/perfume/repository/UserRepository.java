package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<UserSecurityForm> findBySerialIdAndProvider(String serialId, EProvider provider);

    @Query(value = "SELECT u.id as id, u.role as role" +
            " FROM User u WHERE u.serialId = :userId")
    Optional<UserSecurityForm> findUserIdAndRoleBySerialId(@Param("userId") Long userId);

    Optional<UserSecurityForm> findByIdAndRefreshTokenIsNotNull(Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "update User u set u.refreshToken = :refreshToken where u.id = :userId")
    void updateRefreshToken(@Param("userId") Long userId, @Param("refreshToken") String refreshToken);

    interface UserSecurityForm {
        static UserSecurityForm invoke(User user) {
            return new UserSecurityForm() {
                @Override
                public Long getId() {
                    return user.getId();
                }

                @Override
                public ERole getRole() {
                    return user.getRole();
                }

                @Override
                public String getProvider() {
                    return user.getProvider().name();
                }
            };
        }

        Long getId();

        ERole getRole();

        String getProvider();
    }
}
