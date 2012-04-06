import java.util.ArrayList;
import java.util.Iterator;


public class Ordre implements RelationInterface {
	
	private Relation or;
	
	public Ordre()
	{
		this.or = new Relation();
	}
	
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
		this.or = r.clone();
		this.or.cloTrans();
		if (this.or.symetrique())
		{
			throw new IllegalArgumentException("La relation passée en paramètre est cyclique");
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

	public void ajouter(Elt x)
	{
		this.or.ajouter(x, x);
	}
	
	@Override
	public void ajouter(Couple c) {
		if (!this.contient(new Couple(c.getx(),c.gety())))
		{
			if (this.contient(new Couple(c.gety(),c.getx())))
			{
				throw new MathException("Ajout impossible");
			}
		}
		this.ajouter(c.getx());
		this.ajouter(c.gety());
		this.or.ajouter(c.getx(), c.gety());
		this.or.cloTrans();

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
		return or.iterator();
	}
	
	// ajoute x à l'ensemble sous-jacent de la relation d'ordre, ne fait rien si x est déjà dans l'ensemble sous jacent
	public void ajouterAuSousJacent(Elt x){
		
	}
	
	// Enlève x de l'ensemble sous-jacent de la relation d'ordre ainsi que toutes les flèches liées à x
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
	
	public Ensemble major(EnsembleInterface b)
	{
		Ensemble major = (Ensemble) this.depart();
		Iterator<Elt> it = this.maximaux(b).iterator();
		while (it.hasNext())
		{
			major.intersecter(this.or.imageDirecte(it.next()));
		}
		return (major.cardinal() != 0) ? major : null;
	}
	
	public Ensemble minor(EnsembleInterface b)
	{
		Ensemble minor = (Ensemble) this.arrivee();
		Iterator<Elt> it = this.minimaux(b).iterator();
		while (it.hasNext())
		{
			minor.intersecter(this.or.imageReciproque(it.next()));
		}
		return (minor.cardinal() != 0) ? minor : null;
	}
	
	public int nombreDeSommetEntre(Elt x, Elt y)
	{
		
		if (!this.comparable(x, y)) return Integer.MAX_VALUE;
		if (x.estEgalA(y)) return 1;
		ArrayList<Suite> listeChemins = this.or.liestDesChemins(x, y, new Ensemble());
		Iterator<Suite> it = listeChemins.iterator();
		int min  = 50;
		while (it.hasNext())
		{
			Suite temp = it.next();
			if (temp.longueur() < min)
			{
				min = temp.longueur();
			}
		}
		return min;
	}


/** Ex 9.1 comparable */
public boolean comparable(Elt x, Elt y)
{
	
	return this.or.contient(x, y) || this.or.contient(y, x);
}


/** Renvoie le degré de sortie de x pour la Relation courante */
public int degreDeSortie(Elt x) {
	if(!this.depart().contient(x)) throw new MathException("L'élément n'est pas dans l'ensemble de départ");
	int compteur = 0;
	Iterator<Elt> it = this.arrivee().iterator(); 
	while(it.hasNext()){  
		if(this.contient(new Couple(x, it.next()))) compteur ++; 
	} 
	return compteur; 
}

/** Renvoie le degré d'entrée de x pour la Relation courante */
public int degreDEntree(Elt x){
	if(!this.arrivee().contient(x)) throw new MathException("L'élément n'est pas dans l'ensemble d'arrivée");
	int compteur = 0;
	Iterator<Elt> it = this.depart().iterator(); 
	while(it.hasNext()){
		if(this.contient(new Couple(it.next(), x))) compteur++;
	}
	return compteur;
}

}