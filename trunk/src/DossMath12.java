import java.util.Iterator;

public class DossMath12 {
	private static final double BASE = 3800;
	private static final double PRIME = 50;
	private static final double BONUS = 250;
	private static final double DELTA = 0.75;
	private static final Relation COL = Io.chargerRelation("Col.rel");
	private static final Relation FIN = Io.chargerRelation("Fin.rel");
	private static final Relation CPT = Io.chargerRelation("Cpt.rel");
	private static final Relation SUP = Io.chargerRelation("Sup.rel");
	private static final Relation CCN = Io.chargerRelation("Ccn.rel");
	private static final Relation SEX =  Io.chargerRelation("Sex.rel");

	private static final String[] tPers = Io.chargerDta("Pers.dta");
	private static final int nbPers = Integer.parseInt(tPers[0]); // nombre de membres du personnel
	
	private static final String[] tProj = Io.chargerDta("Proj.dta");
	private static final int nbProj = Integer.parseInt(tProj[0]); // nombre d'options
	
	private static final String[] tQual = Io.chargerDta("Qual.dta");
	private static final int nbQual = Integer.parseInt(tQual[0]);// nombre de cours
	
	private static Relation LAP;
	private static Relation affImpossible;
	
	private static Relation PSF;
	
	public static void main(String[] args) throws MathException {
			question1();
	} // main
	
	public static void question1(){
		System.out.println("Question 1");
		System.out.println("******************************************************************************************");
		System.out.println("Réponse 1.1");
		lister(COL.depart().moins(COL.domaine()), "PERSONNELS");
		System.out.println("Réponse 1.2");
		lister(CPT.arrivee().moins(CPT.image()), "QUALIFICATIONS");
		System.out.println("Réponse 1.3");
		if(SUP.acyclique()) System.out.println("La relation SUP est acyclique");
		else System.out.println("La relation SUP n'est pas acyclique");
		System.out.println("Réponse 1.4");
		LAP = (CCN.reciproque()).apres(CPT); // Pas sur
		System.out.println("Réponse 1.5");
		lister(LAP.imageReciproque(new Elt(numéro("Antoinet", "PROJETS"))), "PERSONNELS");
		//Question 1.6, je m'en charge mtn ! (24/03/2012)
		System.out.println("Réponse 1.6");
		//J'aoute un truc juste comme ça :D
		
		//TO DO 
		System.out.println("Réponse 1.7");
		affImpossible = COL.clone();
		affImpossible.enlever(LAP);
		Iterator<Couple> it = affImpossible.iterator();
		System.out.println("Liste des membres du personnel collaborant à un projet ne figurant pas dans la liste de leurs affectations possibles : ");
		if(affImpossible.cardinal()!=0){
			while(it.hasNext()){
				Couple c = it.next();
				lister(affImpossible.imageReciproque(c.getx()), "PERSONNELS");
				System.out.println("Collaborant aux projets : ");
				lister(affImpossible.imageDirecte(c.gety()),"PROJETS");
			}
		}else{
			System.out.println("Aucun membre du personnel ne collabore à un projet ne faisant pas partie de ses affectations possibles.");
		}
	}
	
	public static void question2(){
		System.out.println("Question 2");
		System.out.println("******************************************************************************************");
		System.out.println("Réponse 2.1");
		/** Utiliser LAP 
		 * CAT 1 = Membres sans fleche vers un projet dans FIN
		 * CAT 2 = FIN-LAP ( investissement non lucide )
		 * CAT 3 = Membre dans FIN-LAP et a des fleches supp a cause de FIN-LAP
		 * CAT 4 = Membre qui avait des fleches avant FIN-LAP et qui n'en a plus
		 */
		
		Ensemble cat1 = FIN.depart().clone();
		cat1.enlever(FIN.domaine());
		Ensemble cat2 = FIN.depart().clone();
		// TO DO
	}

	public static void question3(){
		System.out.println("Question 3");
		System.out.println("******************************************************************************************");
		// TO DO
		System.out.println("Question 3.1");
		peutSuivreLaFormationEn();
		System.out.println("peut suivre la formation en (Relation PSF) Initialisé");
				
	}
	
	private static void peutSuivreLaFormationEn(){
		PSF = new Relation(FIN.depart().clone(), CCN.arrivee().clone());
		Iterator<Couple> itFin = FIN.iterator();
		//Iterator<Couple> it = CCN.iterator();
		

		while(itFin.hasNext()){
			Couple couple = itFin.next();
			Elt pers = couple.getx();
			Elt projet = couple.gety(); // y désigne le domaine de qualification
			// Regarder les qualifications demandés pour le projet
			Ensemble qualifProjet = CCN.imageDirecte(projet);
			// Regarder les qualifications du personnel
			Ensemble qualifPers = COL.imageDirecte(pers);
			Iterator<Elt> itQualifProjet = qualifPers.iterator();
			int compteur = 0;
			while(itQualifProjet.hasNext()){
				Elt x = itQualifProjet.next();
				if(!qualifPers.contient(x)){
					PSF.ajouter(pers, x);
				}
				
			}
			
		}

	}

	public static void question4(){
		System.out.println("Question 4");
		System.out.println("******************************************************************************************");
		// TO DO
	}

	public static void question5(){
		System.out.println("Question 5");
		System.out.println("******************************************************************************************");
		// TO DO
	}

	public static void question6(){
		System.out.println("Question 6");
		System.out.println("******************************************************************************************");
		// TO DO
	}

	public static void question7(){
		System.out.println("Question 7");
		System.out.println("******************************************************************************************");
		// TO DO
	}
	


	// affiche à l'écran les éléments de e, interprétés selon contexte
	private static void lister(EnsembleInterface e, String contexte)
			throws MathException {
		String[] t;
		String ct;
		if (contexte.toUpperCase().equals("PERSONNELS")) {
			t = tPers;
			ct = "de membres du personnel";
		} else if (contexte.toUpperCase().equals("PROJETS")) {
			t = tProj;
			ct = "de projets";
		} else if (contexte.toUpperCase().equals("QUALIFICATIONS")) {
			t = tQual;
			ct = "de domaines de qualification";
		} else
			throw new MathException("contexte incorrect");
		if (e.estVide())
			System.out.println("Ensemble vide " + ct);
		else {
			Iterator<Elt> eit = e.iterator();
			while (eit.hasNext()) {
				int x = eit.next().val();
				System.out.println("        " + x + "\t" + t[x]);
			}
		}
	} // lister

	// renvoie le Elt correpondant à nom, d'après contexte; null si incorrect
	private static Elt numéro(String nom, String contexte) throws MathException {
		String[] t;
		int stop;
		if (contexte.toUpperCase().equals("PERSONNELS")) {
			t = tPers;
			stop = nbPers;
		} else if (contexte.toUpperCase().equals("PROJETS")) {
			t = tProj;
			stop = nbProj;
		} else if (contexte.toUpperCase().equals("QUALIFICATIONS")) {
			t = tQual;
			stop = nbQual;
		} else
			throw new MathException("contexte incorrect");
		for (int i = 1; i <= stop; i++)
			if (t[i].toUpperCase().equals(nom.toUpperCase()))
				return new Elt(i);
		return null;
	} // numéro

} // class
