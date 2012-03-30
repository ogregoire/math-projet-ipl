import java.util.Iterator;


public class Ordre implements RelationInterface {
	
	private Relation or;
	
	// construit l'identité sur e
	public Ordre(EnsembleInterface e){
		this.or = Relation.identite(e);
	}
	
	// constructeur par recopie
	public Ordre(Ordre or){
		this.or = or.or.clone(); 
	}
	
	// Construit le plus petit ordre contenant r
	// génère une MathException si cette construction est impossible
	public Ordre(Relation r){
		Relation relATest = r.clone();
		relATest.cloTrans();
		if(relATest.antisymetrique()){
			 relATest.cloReflex();
			 this.or=relATest.clone();
		}
		throw new MathException();
	}
	
	@Override
	public boolean estVide() {
		return this.or.estVide();
	}

	@Override
	public boolean contient(Couple c) {
		return this.or.contient(c);
	}

	@Override
	public void ajouter(Couple c) {
		if(!this.depart().contient(c.getx())){
			throw new MathException("Impossible d'ajouter");
		}
		if(!this.depart().contient(c.gety())){
			throw new MathException("Impossible d'ajouter");
		}
		if(!this.contient(c)){
			if(this.or.contient(c.gety(), c.getx())){
				throw new MathException("Impossible d'ajouter");
			}
			this.or.ajouter(c);
			this.or.cloTrans();
		}

	}

	@Override
	public void enlever(Couple c) {

	}

	public boolean estUneAreteDeHasse(Elt x, Elt y){
		if(!this.or.contient(x, y)){
			return false;
		}
		if(x.estEgalA(y)){
			return false;
		}
		
		Iterator<Elt> it = this.depart().moins(new Ensemble(x, y)).iterator();
		while (it.hasNext())
		{
			Elt z = it.next();
			if (this.contient(new Couple(x, z)) && this.contient(new Couple(z, y)))
			{
				return false;
			}
		}
		return true;
		
	}
	
	@Override 
	public EnsembleInterface depart() {
		return this.or.depart();
	}

	@Override
	public EnsembleInterface arrivee() {
		return this.or.arrivee();
	}

	@Override
	public Iterator<Couple> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// ajoute x à l'ensemble sous-jacent de la relation d'ordre, ne fait rien si x est déjà dans l'ensemble sous jacent
	public void ajouterAuSousJacent(Elt x){
		
	}
	
	// Enlève x de l'ensemble sous-jacent de la relation d'ordre ainsi que toutes les flèches liées à x
	public void enleveDuSouSJacent(Elt x){
		
	}

}
