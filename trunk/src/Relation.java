/** Classe Relation héritant de RelationDeBase
	 Fournit des outils de manipulation des relations entre sous-ensembles de l'Univers

	 @author M.Marchand
	 @version mars 2008
 */

import java.util.*;
import java.io.*;

public class Relation extends RelationDeBase {

	/** Valeur numérique de MAXELT */
	private static final int MAX = Elt.MAXELT.val();

	/** Construit la Relation vide sur l'ensemble vide */
	public Relation() {
		super();
	}

	/** Construit la Relation vide de d vers a */
	public Relation(EnsembleInterface d, EnsembleInterface a) {
		super(d, a);
	}

	/** Constructeur par recopie */
	public Relation clone() {
		return (Relation) super.clone();
	}

	
	/** Construit l'identité */
	public static Relation identite(EnsembleInterface e)
	{
		Relation id = new Relation(e, e);
		Iterator<Elt> it = e.iterator();
		while (it.hasNext())
		{
			Elt x = it.next();
			id.ajouter(x, x);
		}
		return id;
	}
	
	/** Renvoie le degré de sortie de x pour la Relation courante */
	public int degreDeSortie(Elt x) {
		if(!this.depart().contient(x)) throw new MathException("L'élément n'est pas dans l'ensemble de départ");
		int compteur = 0;
		Iterator<Elt> it = this.arrivee().iterator(); 
		while(it.hasNext()){  
			if(this.contient(x, it.next())) compteur ++; 
		} 
		return compteur; 
	}
	
	/** Renvoie le degré d'entrée de x pour la Relation courante */
	public int degreDEntree(Elt x){
		if(!this.arrivee().contient(x)) throw new MathException("L'élément n'est pas dans l'ensemble d'arrivée");
		int compteur = 0;
		Iterator<Elt> it = this.depart().iterator(); 
		while(it.hasNext()){
			if(this.contient(it.next(), x)) compteur++;
		}
		return compteur;
	}

	/** Renvoie l’image directe de e par la Relation courante */
	public Ensemble imageDirecte(EnsembleInterface e){
		Ensemble ens = new Ensemble();
		Iterator<Elt> itArr = this.arrivee().iterator();
		while(itArr.hasNext()){
			Elt eltArr = itArr.next();
			Iterator<Elt> itEns = e.iterator();
			while(itEns.hasNext()){
				Elt eltEns = itEns.next();
				if(this.contient(eltEns, eltArr)){
					ens.ajouter(new Elt(eltArr));
					break;
				}
			}
		}
		return ens;
	}
	
	/** Renvoie l’image reciproque de e par la Relation courante */
	public Ensemble imageReciproque(EnsembleInterface e){
		Ensemble imageRec = new Ensemble();
		Iterator<Elt> it1 = e.iterator();
		while (it1.hasNext())
		{
			Elt x = it1.next();
			imageRec.ajouter(imageReciproque(x));
		}
		return imageRec;
	}
	
	/** Renvoie l'image reciproque de x par la Relation Courante */
	public Ensemble imageReciproque(Elt x)
	{
		if (x == null)
		{
			throw new IllegalArgumentException();
		}
		if (!this.arrivee().contient(x))
		{
			throw new IllegalArgumentException("L'Elt entré n'est pas dans l'ensemble d'arrivée.");
		}
		Ensemble imageRéciproque = new Ensemble();
		Iterator<Elt> it = this.depart().iterator();
		while (it.hasNext())
		{
			Elt e = it.next();
			if (this.contient(e, x))
			{
				imageRéciproque.ajouter(e);
			}
		}
		return imageRéciproque;
	}
	
	/** Renvoie l’image de la Relation courante */
	public Ensemble image(){
		return this.imageDirecte(this.depart());
	}
	
	/** Renvoie le domaine de la Relation courante */
	public Ensemble domaine(){
		return this.imageReciproque(this.arrivee());
	}
	
	/**  Renvoie l’image directe de {i} par la Relation courante */
	public Ensemble imageDirecte(Elt i){
		Ensemble ens = new Ensemble();
		ens.ajouter(i);
		return this.imageDirecte(ens);	
	}
	
	
	
	/** Renvoie la complémentaire de la Relation courante */
	public Relation complementaire(){
		Relation rel = new Relation(this.depart(), this.arrivee());
		Iterator<Elt> itDeb = this.depart().iterator();
		while(itDeb.hasNext()){
			Elt eltDeb = itDeb.next();
			Iterator<Elt> itArr = this.arrivee().iterator();
			while(itArr.hasNext()){
				Elt eltArr = itArr.next();
				if(!this.contient(eltDeb,eltArr)){
					rel.ajouter(eltDeb, eltArr);
				}
			}
		}
		return rel;
	}
	
	/** Renvoie la réciproque de la Relation courante */
	public Relation reciproque(){
		Relation rel = new Relation(this.arrivee(),this.depart());
		Iterator<Couple> it = this.iterator();
		while(it.hasNext()){
			rel.ajouter(it.next().reciproque());
		}
		return rel;
	}
	
	/** Si possible, remplace la Relation courante par son union avec r */ 
	public void ajouter(RelationInterface r){
		
		if(!this.depart().estEgalA(r.depart()) || !this.arrivee().estEgalA(r.arrivee())) throw new MathException("Les ensembles de début/arrivée ne sont pas les mêmes !");
		
		Iterator<Elt> itA = this.arrivee().iterator();
		while(itA.hasNext()){
			Elt eltA = itA.next();
			Iterator<Elt> itD = this.depart().iterator();
			while(itD.hasNext()){
				Elt eltD = itD.next();
				if(r.contient(new Couple(eltD,eltA))) this.ajouter(eltD, eltA);
			}
		}
	}
	
	/** si possible, remplace this par sa différence avec r */
	
	public void enlever(RelationInterface r){
		
		if(!this.depart().estEgalA(r.depart()) || !this.arrivee().estEgalA(r.arrivee())) throw new MathException("Les ensembles de début/arrivée ne sont pas les mêmes !");
		
		Iterator<Couple> itR = r.iterator();
		while(itR.hasNext()){
			Couple coupR = itR.next();
			if(this.contient(coupR)){
				this.enlever(coupR);
			}
		}
		
//		Enlever mais avec Elt
//		Iterator<Elt> itRD = r.depart().iterator();
//		while(itRD.hasNext()){
//			Elt eltRD = itRD.next();
//			Iterator<Elt> itRA = r.arrivee().iterator();
//			while(itRA.hasNext()){
//				Elt eltRA = itRA.next();
//				Couple couple = new Couple(eltRD, eltRA);
//				if(r.contient(couple)) this.enlever(couple);
//			}
//		}
	}
	
	/** Si possible, remplace this par son intersection avec r */
	public void intersecter(RelationInterface r){
		if(!this.depart().estEgalA(r.depart()) || !this.arrivee().estEgalA(r.arrivee())) throw new MathException("Les ensembles de début/arrivée ne sont pas les mêmes !");
		Iterator<Couple> it = this.iterator();
		Relation aSupprimer = new Relation(this.depart(),this.arrivee());
		
		while(it.hasNext()){
			Couple c = it.next();
			if(!r.contient(c)){
				aSupprimer.ajouter(c);
			}
		}
		Iterator<Couple> itASup = aSupprimer.iterator();
		while(itASup.hasNext()){
			Couple c = itASup.next();
			this.enlever(c);
		}
	}
	
	/** Si possible, renvoye la composée "this après r" */
	public Relation apres(RelationInterface r){ //OK
		
		if(!this.depart().estEgalA(r.arrivee())) throw new MathException("Les ensembles de début/arrivée ne sont pas les mêmes !");
		
		Relation nouvelleRel = new Relation(r.depart(),this.arrivee());
		Iterator<Elt> itD = nouvelleRel.depart().iterator();
		while(itD.hasNext()){
			Elt eltD = itD.next();
			Iterator<Elt> itA = nouvelleRel.arrivee().iterator();
			while(itA.hasNext()){
				Elt eltA = itA.next();
				Iterator<Elt> itM = this.depart().iterator();
				while(itM.hasNext()){
					Elt eltM = itM.next();
					if(r.contient(new Couple(eltD,eltM))&& this.contient(new Couple(eltM,eltA))){
						nouvelleRel.ajouter(eltD, eltA);
						break;
					}
				}
			}
		}
		
		return nouvelleRel;
		
	}
	
	/** Renvoye vrai si la relation est fonctionnelle ( degre de sortie < 1 ) */
	public boolean fonctionnelle(){
		Iterator<Elt> it = this.depart().iterator();
		while(it.hasNext()){
			if(this.degreDeSortie(it.next()) > 1) return false;
		}
		return true;
	}
	

	/** renvoie true ssi  this est inclus dans r */
	public boolean inclusDans(RelationInterface r){
		if(r == null) return false;
		if(!this.depart().inclusDans(r.depart())) return false;
		if(!this.arrivee().inclusDans(r.depart())) return false;
		Iterator<Couple> it = this.iterator();
		while(it.hasNext()){
			if(!r.contient(it.next())) return false;
		}
		return true;
	}
	/** Renvoie true ssi  this = r */
	public boolean estEgaleA(RelationInterface r){
		 if (!this.depart().estEgalA(r.depart())) return false; 
	      if (!this.arrivee().estEgalA(r.arrivee())) return false; 
	      Iterator<Elt> itd = this.depart().iterator(); 
	      while (itd.hasNext()){ 
	         Elt x = itd.next(); 
	         Iterator<Elt> ita = this.arrivee().iterator(); 
	         while (ita.hasNext()){ 
	            Elt y = ita.next(); 
	            if (r.contient(new Couple(x,y)) != this.contient(x,y)) return false; 
	         } 
	      } 
	      return true;
	}
	
	/**  renvoie true ssi la restriction de this à e est réflexive */
	public boolean reflexive(EnsembleInterface e){
		if(!e.inclusDans(this.depart())) throw new MathException();
		Iterator<Elt> it = e.iterator();
		while(it.hasNext()){
			Elt elem = it.next();
			if(!this.contient(elem, elem)) return false;
		}
		return true;
	}
	
	/**  renvoie true ssi this est réflexive */
	public boolean reflexive(){
		if(!this.depart().estEgalA(this.arrivee())) throw new MathException();
		Iterator<Elt> it = this.depart().iterator();
		while(it.hasNext()){
			Elt x = it.next();
			if(!this.contient(x, x)) return false;
		}
		return true;
	}
	
	/**  renvoie true ssi this est antiréflexive */
	public boolean antireflexive(){
		if(!this.depart().estEgalA(this.arrivee())) throw new MathException();
		 Iterator<Elt> it = this.depart().iterator();
		 while(it.hasNext()){
			 Elt x = it.next();
			 if(this.contient(x, x)) return false;
		 }
		 return true;
	}
	
	/**  renvoie true ssi this est symétrique */
	public boolean symetrique(){
		if(!this.depart().estEgalA(this.arrivee())) throw new MathException();
		Iterator<Couple> it = this.iterator();
		while(it.hasNext()){
			Couple c = it.next(); 
			if(!this.contient(c.gety(), c.getx())) return false; 
		}
		return true;
	}
	
	/** renvoie true ssi this est antisymétrique */
	public boolean antisymetrique(){
		if(!this.depart().estEgalA(this.arrivee())) throw new MathException();
		Iterator<Couple> it = this.iterator();
		while(it.hasNext()){
			Couple c = it.next();
			if(this.contient(c.gety(), c.getx())){
				if(!c.gety().estEgalA(c.getx())) return false;
			}
		}
		return true;
	}
	
	/** renvoie true ssi  this est transitive */
	public boolean transitive(){ // ok
		 if(!this.depart().estEgalA(this.arrivee())) throw new MathException();
		 
		 Iterator<Couple> itCouple = this.iterator();
		 while(itCouple.hasNext()){
			 Couple c = itCouple.next();
				Iterator<Elt> itElt = this.depart().iterator();
				while(itElt.hasNext()){
					Elt eltC = itElt.next();
					if(this.contient(c.gety(), eltC) && !this.contient(c.getx(), eltC)) return false;
				}
			 }
		 return true;
	}
	
	/**  renvoie true ssi  this est circulaire */
	public boolean circulaire(){
		if(!this.depart().estEgalA(this.arrivee())) throw new MathException();
		
		Iterator<Couple> itCouple = this.iterator();
		while (itCouple.hasNext())
		{
			Couple c = itCouple.next();
			Iterator<Elt> itElt = this.depart().iterator();
			while (itElt.hasNext())
			{
				Elt elem = itElt.next();
				if (this.contient(c.getx(), elem) && this.contient(elem, c.gety()) && !this.contient(c.gety(), c.getx())) return false;
			}
		}
		return true;

	
	}
	
	
	/** Clôture la Relation courante pour la réflexivité */
	public void cloReflex() {
		if (!this.depart().estEgalA(this.arrivee()))
			throw new MathException("Hors sujet : cloRéflex()");
		Iterator<Elt> it = this.depart().iterator();
		while (it.hasNext()) {
			Elt x = it.next();
			this.ajouter(x, x);
		}
	}

	/** Clôture la Relation courante pour la symétrie */
	public void cloSym() {
		if (!this.depart().estEgalA(this.arrivee()))
			throw new MathException("Hors sujet : cloSym()");
		Iterator<Elt> itd = this.depart().iterator();
		EnsembleInterface reste = new Ensemble(this.depart());
		while (itd.hasNext()) {
			Elt x = itd.next();
			reste.enlever(x);
			Iterator<Elt> it = reste.iterator();
			while (it.hasNext()) {
				Elt y = it.next();
				if (this.contient(x, y) || this.contient(y, x)) {
					this.ajouter(x, y);
					this.ajouter(y, x);
				}
			}
		}
	}


	/** Clôture la Relation courante pour la transitivité (Warshall) */
	public void cloTrans() {
		if (!this.depart().estEgalA(this.arrivee()))
			throw new MathException("Hors sujet : cloTrans()");
		Iterator<Elt> it1 = this.depart().iterator();
		while (it1.hasNext()) {
			Elt x = it1.next();
			Iterator<Elt> it2 = this.depart().iterator();
			while (it2.hasNext()) {
				Elt y = it2.next();
				if (this.contient(y, x)) {
					Iterator<Elt> it3 = this.depart().iterator();
					while (it3.hasNext()) {
						Elt z = it3.next();
						if (this.contient(x, z))
							this.ajouter(y, z);
					}
				}
			}
		}
	}
	
	/** Renvoie true si acyclique */
	public boolean acyclique()
	{
		Relation clone = this.clone();
		clone.cloTrans();
		return clone.antireflexive();
	}

	/** Renvoie true si faiblement connexe */
	public boolean faiblementConnexe()
	{
		Relation clone = this.clone();
		clone.cloSym();
		clone.cloTrans();
		return clone.complementaire().estVide();
	}

	/** Renvoie true si fortement connexe */
	public boolean fortementConnexe()
	{
		Relation clone = this.clone();
		clone.cloTrans();
		return clone.complementaire().estVide();
	}

	/** Renvoie true si connexe */
	public boolean connexe()
	{
		return this.symetrique() && this.fortementConnexe();
	}


} // class Relation