package springstuff.sybase;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="tabela3syb")
public class Clasa3 {

	@Id
	Long id;
	String telefon;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	
}
