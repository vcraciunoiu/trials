package springstuff;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import springstuff.sybase.Clasa3;
import springstuff.sybase.Clasa3Repository;
import springstuff.sybase.ClasaDoi;
import springstuff.sybase.ClasaDoiRepository;
import springstuff.sybase.ClasaUnu;
import springstuff.sybase.ClasaUnuRepository;

@Service
public class SybaseService {

	@Resource
	ClasaUnuRepository clasaUnuRepository;
	
	@Resource
	ClasaDoiRepository clasaDoiRepository;
	
	@Resource
	Clasa3Repository clasa3Repository;
	
	@Transactional
	public void faInserturile() {
		ClasaUnu clasaunu = new ClasaUnu();
		clasaunu.setNume("numedeclasaunu");
		ClasaUnu savedclasaunu = clasaUnuRepository.save(clasaunu);
		
		ClasaDoi clasadoi = new ClasaDoi();
		clasadoi.setId(savedclasaunu.getId());
		clasadoi.setComentariu("comentariude clasa doi");
		clasaDoiRepository.save(clasadoi);
		
		Clasa3 clasatrei = new Clasa3();
		clasatrei.setId(savedclasaunu.getId());
		clasatrei.setTelefon("54352345234");
		clasa3Repository.save(clasatrei);
	}
}
