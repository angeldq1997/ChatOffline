package chat;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class User {
    private String name;

    public User() { } // JAXB necesita un constructor sin argumentos.

    public User(String name) { this.name = name; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
