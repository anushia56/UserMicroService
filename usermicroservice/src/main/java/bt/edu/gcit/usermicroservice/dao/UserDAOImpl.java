package bt.edu.gcit.usermicroservice.dao;

import bt.edu.gcit.usermicroservice.entity.User;
import org.springframework.stereotype.Repository;
// import javax.persistence.EntityManager;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

@Repository
public class UserDAOImpl implements UserDAO {
    private EntityManager entityManager;
    
    @Autowired
    public UserDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
 
    @Override
    public User save(User user) {
        return entityManager.merge(user);
    }
} 
