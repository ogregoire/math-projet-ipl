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
			rr.cloSym();
			rr.cloTrans();
			rr.cloReflex();
			Iterator<Elt> it = sousjac.iterator();
			while (it.hasNext())
			{
				Elt x = it.next();
				Elt rep = new Elt(MAX);
				do
					rep = rep.succ(); // recherche du représentant
				while (!rr.contient(x, rep)); // de la classe de x
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
		if (sousjac.contient(x) && sousjac.contient(y))
		{
			Elt rx = tabRep[x.val()];
			Elt ry = tabRep[y.val()];
			for (int i = 1; i <= MAX; i++)
			{
				if (sousjac.contient(new Elt(i)) && tabRep[i].estEgalA(rx)) tabRep[i] = ry;
			}
		}
		else if (sousjac.contient(x))
		{
			sousjac.ajouter(y);
			tabRep[y.val()] = tabRep[x.val()];
		}
		else if (sousjac.contient(y))
		{
			sousjac.ajouter(x);
			tabRep[x.val()] = tabRep[y.val()];
		}
		else
		{
			sousjac.ajouter(x);
			sousjac.ajouter(y);
			tabRep[x.val()] = x;
			tabRep[y.val()] = x;
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
		return this.tabRep[x.val()].estEgalA(this.tabRep[y.val()]);
		//return this.tabRep[x.val()] == this.tabRep[y.val()];
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
		if (this.contient(x, y) && this.classe(x).cardinal() == 2)
		{
			this.tabRep[x.val()] = x;
			this.tabRep[y.val()] = y;
		}
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
}
