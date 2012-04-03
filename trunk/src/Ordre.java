import java.util.Iterator;


public class Ordre implements RelationInterface {
	
	private Relation or;
	
	// construit l'identit� sur e
	public Ordre(EnsembleInterface e){
		this.or = Relation.identite(e);
	}
	
	// constructeur par recopie
	public Ordre(Ordre or){
		this.or = or.or.clone(); 
	}
	
	// Construit le plus petit ordre contenant r
	// g�n�re une MathException si cette construction est impossible
	public Ordre(Relation r){
		this.or = r.clone();
		this.or.cloTrans();
		if (this.or.symetrique())
		{
			throw new IllegalArgumentException("La relation pass�e en param�tre est cyclique");
		}
		this.or.cloReflex();
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
	
	// ajoute x � l'ensemble sous-jacent de la relation d'ordre, ne fait rien si x est d�j� dans l'ensemble sous jacent
	public void ajouterAuSousJacent(Elt x){
		
	}
	
	// Enl�ve x de l'ensemble sous-jacent de la relation d'ordre ainsi que toutes les fl�ches li�es � x
	public void enleveDuSouSJacent(Elt x){
		
	}
	
	public Ensemble maximaux(EnsembleInterface b)
	{
		Ensemble max = new Ensemble();
		Iterator<Elt> it1 = b.iterator();
		while (it1.hasNext())
		{
			Elt x = it1.next();
			boolean ok = true;
			Iterator<Elt> it2 = new Ensemble(max).iterator();
			while (it2.hasNext() && ok)
			{
				Elt y = it2.next();
				if (this.contient(new Couple(x, y)))
				{
					ok = false;
				}
				if (this.contient(new Couple(y, x)))
				{
					max.enlever(y);
				}
			}
			if (ok)
			{
				max.ajouter(x);
			}
		}
		return max;
	}
	
	public Elt maximum(EnsembleInterface b)
	{
		Ensemble max = this.maximaux(b);
		if (max.cardinal() == 1) return max.unElement();
		return null;
	}
	
	public Ensemble minimaux(EnsembleInterface b)
	{
		Ensemble min = new Ensemble();
		Iterator<Elt> it1 = b.iterator();
		while (it1.hasNext())
		{
			Elt x = it1.next();
			boolean ok = true;
			Iterator<Elt> it2 = new Ensemble(min).iterator();
			while (it2.hasNext() && ok)
			{
				Elt y = it2.next();
				if (this.contient(new Couple(x, y)))
				{
					min.enlever(y);
				}
				if (this.contient(new Couple(y, x)))
				{
					ok = false;
				}
			}
			if (ok)
			{
				min.ajouter(x);
			}
		}
		return min;
	}
	
	public Elt minimum(EnsembleInterface b)
	{
		Ensemble min = this.minimaux(b);
		if (min.cardinal() == 1) return min.unElement();
		return null;
	}
	

}
