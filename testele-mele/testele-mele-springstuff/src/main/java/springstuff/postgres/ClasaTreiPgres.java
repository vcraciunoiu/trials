package springstuff.postgres;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="tabela3pgres")
public class ClasaTreiPgres {

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
