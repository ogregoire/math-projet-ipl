import java.util.*;

public class Equivalence implements RelationInterface
{
	private Ensemble			sousjac;				// ensemble sous-jacent
	private Elt[]				tabRep;				// tableau des
														// représentants
	private static final int	MAX	= Elt.MAXELT.val();

	public Equivalence(EnsembleInterface e)
	{
		this.sousjac = new Ensemble(e);
		this.tabRep = new Elt[MAX + 1];
		for (int i = 1; i <= MAX; i++)
		{
			Elt ei = new Elt(i);
			if (e.contient(ei)) this.tabRep[i] = ei;
		}
	}

	public Equivalence(RelationInterface r)
	{
		if (!r.depart().estEgalA(r.arrivee())) throw new MathException("Construction impossible");
		this.sousjac = (Ensemble) r.depart();
		this.tabRep = new Elt[MAX + 1];
		if (!sousjac.estVide())
		{
			Relation rr = ((Relation) r).clone();
			rr.cloReflex();
			rr.cloSym();
			rr.cloTrans();
			
			Iterator<Elt> it = sousjac.iterator();
			while (it.hasNext())
			{
				Elt x = it.next();
				Elt rep = new Elt(MAX);
				do{
					rep = rep.succ(); // recherche du représentant
					System.out.println("test  rep : " + rep.val());
				}while (rr.contient(x, rep)); // de la classe de x
				this.tabRep[x.val()] = rep;
			}
		}
	}

	public Relation après(Relation r)
	{
		Relation nouvelle = new Relation();
		Iterator<Elt> it1 = r.depart().iterator();
		while (it1.hasNext())
		{
			Elt x = it1.next();
			Iterator<Elt> it2 = r.imageDirecte(x).iterator();
			while (it2.hasNext())
			{
				Ensemble temp = this.classe(it2.next());
				Iterator<Elt> it3 = temp.iterator();
				while (it3.hasNext())
				{
					nouvelle.ajouter(x, it3.next());
				}
			}
		}
		return nouvelle;
	}

	@Override
	public void ajouter(Couple arg0)
	{
		this.ajouter(arg0.getx(), arg0.gety());
	}

	public void ajouter(Elt x, Elt y)
	{
		if (!sousjac.contient(x)) throw new MathException();
		   if (!sousjac.contient(y)) throw new MathException();
		   if (!this.contient(x,y)){
		      Elt rx = tabRep[x.val()];
		      Iterator<Elt> itS = sousjac.iterator();
		      while (itS.hasNext()){
		         Elt e = itS.next();
		         if (tabRep[e.val()].estEgalA(rx))
		             tabRep[e.val()] = tabRep[y.val()];
		      }	
		   }
	}

	public void ajouterSommet(Elt x)
	{
		if (!this.sousjac.contient(x))
		{
			this.sousjac.ajouter(x);
			this.tabRep[x.val()] = x;
		}
	}

	@Override
	public EnsembleInterface arrivee()
	{
		return new Ensemble();
	}

	@Override
	public boolean contient(Couple arg0)
	{
		return this.contient(arg0.getx(), arg0.gety());
	}

	public boolean contient(Elt x, Elt y)
	{
		  if (!sousjac.contient(x)) throw new MathException();
		  if (!sousjac.contient(y)) throw new MathException();
		  return tabRep[x.val()].estEgalA(tabRep[y.val()]);

	}

	@Override
	public EnsembleInterface depart()
	{
		return new Ensemble();
	}

	@Override
	public void enlever(Couple arg0)
	{
		this.enlever(arg0.getx(), arg0.gety());
	}

	public void enlever(Elt x, Elt y)
	{
		 if (this.contient(x,y) && this.classe(x).cardinal() == 2){
			 this.tabRep[x.val()] = x; this.tabRep[y.val()] = y;   
		}
		   else throw new MathException();
	}

	@Override
	public boolean estVide()
	{
		return this.sousjac.estVide();
	}

	public Ensemble classe(Elt x)
	{
		if (sousjac.contient(x))
		{
			Ensemble cx = new Ensemble();
			Iterator<Elt> it = sousjac.iterator();
			while (it.hasNext())
			{
				Elt y = it.next();
				if (this.contient(x, y)){
					cx.ajouter(y);
				}
			
			}
			return cx;
		}
		else return null;
	}
	
	public int nbreClasses()
	{
		Ensemble dejaVu = new Ensemble();
		int nbreClasses = 0;
		for (int i = 1; i <= MAX; i++)
		{
			if (this.sousjac.contient(new Elt(i)))
			{
				Elt e = this.tabRep[i];
				if (!dejaVu.contient(e))
				{
					nbreClasses++;
					dejaVu.ajouter(e);
				}
			}
		}

		return nbreClasses;
	}

	public Ensemble[] quotient()
	{
		Ensemble[] p = new Ensemble[sousjac.cardinal()];
		Ensemble reste = new Ensemble(sousjac);
		int i = 0;
		while (!reste.estVide())
		{
			p[i] = this.classe(reste.unElement());
			reste.enlever(p[i]);
			i++;
		}
		Ensemble[] quot = new Ensemble[i];
		for (int j = 0; j < i; j++)
			quot[j] = p[j];
		return quot;
	}

	public void intersecter(Equivalence autreEq)
	{
		this.sousjac.intersecter(autreEq.sousjac);
		Ensemble reste = new Ensemble(sousjac);
		while (!reste.estVide())
		{
			Elt x = reste.unElement();
			Ensemble ex = new Ensemble();
			Iterator<Elt> it = reste.iterator();
			while (it.hasNext())
			{
				Elt y = it.next();
				if (this.contient(x, y) && autreEq.contient(x, y))
				{
					this.tabRep[y.val()] = x;
					ex.ajouter(y);
				}
			}
			reste.enlever(ex);
		}
	}

	@Override
	public Iterator<Couple> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void ajouterAuSousJacent(Elt x){
		if (!sousjac.contient(x)){      
			this.sousjac.ajouter(x);     
			this.tabRep[x.val()] = x;   
		}	
	}

	public void enleverDuSousJacent(Elt x){
	   if (!sousjac.contient(x)) return;
	   if (this.tabRep[x.val()].estEgalA(x)){
	      Ensemble classe = this.classe(x);
	      classe.enlever(x);
	      if (!classe.estVide()){
	         Elt rep = classe.unElement();
	         Iterator<Elt> it = classe.iterator();
	         while(it.hasNext()){
	            this.tabRep[it.next().val()] = rep;
	         }
	      } 
	   }
	   this.tabRep[x.val()] = null;
	   this.sousjac.enlever(x);   
	}

}
