package ru.asteises.pickerauth2.repisitoryes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.asteises.pickerauth2.model.Role;

@Repository
public interface RoleStorage extends JpaRepository<Role, String> {

    Role findRoleByName(String name);
}
