package springstuff;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import springstuff.postgres.ClasaDoiPgres;
import springstuff.postgres.ClasaDoiPgresRepo;
import springstuff.postgres.ClasaTreiPgres;
import springstuff.postgres.ClasaTreiPgresRepo;
import springstuff.postgres.ClasaUnuPgres;
import springstuff.postgres.ClasaUnuPgresRepo;

@Service
public class PostgresService {

	@Resource
	ClasaUnuPgresRepo clasaUnuRepository;
	
	@Resource
	ClasaDoiPgresRepo clasaDoiRepository;
	
	@Resource
	ClasaTreiPgresRepo clasaTreiRepository;
	
	@Transactional
	public void faInserturile() {
		ClasaUnuPgres clasaunu = new ClasaUnuPgres();
		clasaunu.setNume("numedeclasaunu");
		ClasaUnuPgres savedclasaunu = clasaUnuRepository.save(clasaunu);
		
		ClasaDoiPgres clasadoi = new ClasaDoiPgres();
		clasadoi.setId(savedclasaunu.getId());
		clasadoi.setComentariu("comentariude clasa doi");
		clasaDoiRepository.save(clasadoi);
		
		ClasaTreiPgres clasatrei = new ClasaTreiPgres();
		clasatrei.setId(savedclasaunu.getId());
		clasatrei.setTelefon("54352345234");
		clasaTreiRepository.save(clasatrei);
	}
}
