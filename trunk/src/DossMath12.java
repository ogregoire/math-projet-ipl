import java.util.ArrayList;
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
	private static Ordre hierarchie;
	private static Ordre test;
	
	public static void main(String[] args) throws MathException {
			question1();
			question2();
			question3();
			question4();
			question5();
			test();
			question6();
			question7();
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
		int max = 0;
		Iterator<Elt> itMax = LAP.domaine().iterator();
		while(itMax.hasNext()){
			max = Math.max(max, LAP.degreDeSortie(itMax.next()));
		}
		itMax=LAP.domaine().iterator();
		Ensemble ens = new Ensemble();
		while(itMax.hasNext()){
			Elt elem = itMax.next();
			if(LAP.degreDeSortie(elem) == max) ens.ajouter(elem);
		}
		System.out.println("Membres du personnel ayant le maximum d'affectations possibles");
		lister(ens, "PERSONNELS");
		
		System.out.println("Réponse 1.7");
		affImpossible = COL.clone();
		affImpossible.enlever(LAP);
		if(affImpossible.cardinal()!=0){
			Iterator<Couple> it = affImpossible.iterator();
			System.out.println("Liste des membres du personnel collaborant à un projet ne figurant pas dans la liste de leurs affectations possibles : ");
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
		System.out.println("\nQuestion 2");
		System.out.println("******************************************************************************************");
		System.out.println("Réponse 2.1");
		
		Relation finLu = new Relation(FIN.depart(), FIN.arrivee());
		Iterator<Couple> itLu = FIN.iterator();
		while(itLu.hasNext()){
			Couple c = itLu.next();
			if(LAP.contient(c)){
				finLu.ajouter(c);
			}
		}
		
		Ensemble cat1 = FIN.depart().clone();
		cat1.enlever(FIN.domaine());
		
		Ensemble cat2 = FIN.domaine().clone();
		Iterator<Couple> it = FIN.iterator();
		while(it.hasNext()){
			Couple c = it.next();
			if(LAP.contient(c)){
				cat2.enlever(c.getx());
			}
		}
		
		Ensemble cat3 = new Ensemble();
		it = FIN.iterator();
		while(it.hasNext() ){
			Couple c = it.next();
			if(LAP.contient(c) && FIN.degreDeSortie(c.getx())>finLu.degreDeSortie(c.getx())){
				cat3.ajouter(c.getx());
			}
		}
		
		Ensemble cat4 = new Ensemble();
		it = FIN.iterator();
		while(it.hasNext() ){
			Couple c = it.next();
			if(LAP.contient(c) && FIN.degreDeSortie(c.getx())==finLu.degreDeSortie(c.getx())){
				cat4.ajouter(c.getx());
			}
		}
		
		
		System.out.println("Catégorie 1 : ");
		lister(cat1, "PERSONNELS");
		System.out.println("Catégorie 2 : ");
		lister(cat2, "PERSONNELS");
		System.out.println("Catégorie 3 : ");
		lister(cat3, "PERSONNELS");
		System.out.println("Catégorie 4 : ");
		lister(cat4, "PERSONNELS");
		
		System.out.println("Réponse 2.2");
		
		double compteur1f =0;
		double compteur2f =0;
		double compteur3f =0;
		double compteur4f =0;
		
		Elt femme = new Elt(2);
		Iterator<Couple> itSex = SEX.iterator();
		while(itSex.hasNext()){
			Couple c = itSex.next();
			if(cat1.contient(c.getx())){
				if(c.gety().estEgalA(femme)){
					compteur1f++;
				}
			}else{
				if(cat2.contient(c.getx())){
					if(c.gety().estEgalA(femme)){
						compteur2f++;
					}
				}else{
					if(cat3.contient(c.getx())){
						if(c.gety().estEgalA(femme)){
							compteur3f++;
						}
					}else{
						if(cat4.contient(c.getx())){
							if(c.gety().estEgalA(femme)){
								compteur4f++;
							}
						}
					}
				}
			}
		}
		
		compteur1f = compteur1f/cat1.cardinal();
		compteur2f = compteur2f/cat2.cardinal();
		compteur3f = compteur3f/cat3.cardinal();
		compteur4f = compteur4f/cat4.cardinal();
		
		System.out.println("La/les catégorie(s) comportant la plus grande proportion de femmes est/sont : ");
		double max = Math.max(Math.max(Math.max(compteur1f, compteur2f), compteur3f), compteur4f);
		if(compteur1f==max){
			System.out.println("Catégorie 1 : Aucun financement ");
		}
		if(compteur2f==max){
			System.out.println("Catégorie 2 : Aucun soutien financier lucide");
		}
		if(compteur3f==max){
			System.out.println("Catégorie 3 : Des soutiens financiers lucides et d'autres soutiens financiers ");
		}
		if(compteur4f==max){
			System.out.println("Catégorie 4 : Seulement des soutiens financiers lucides");
		}
		
	}

	public static void question3(){
		System.out.println("\nQuestion 3");
		System.out.println("******************************************************************************************");
		// TO DO
		System.out.println("Question 3.1");
		peutSuivreLaFormationEnInit();
		System.out.println("peut suivre la formation en (Relation PSF) Initialisé");
		System.out.println("Question 3.2");
		lister(PSF.imageDirecte(numéro("KOEKELBERG Basile", "PERSONNELS")), "QUALIFICATIONS");
		System.out.println("Question 3.3");
		Ensemble quest33 = new Ensemble();
		Iterator<Elt> it = PSF.depart().iterator();
		while(it.hasNext()){
			Elt el = it.next();
			if(PSF.degreDeSortie(el) == 0){
				quest33.ajouter(el);
			}
		}
		lister(quest33,"PERSONNELS");
	}
	
	
	private static void peutSuivreLaFormationEnInit(){
		PSF = new Relation(COL.depart().clone(), CCN.arrivee().clone());
		Iterator<Couple> itCol = COL.iterator();
		//Iterator<Couple> it = CCN.iterator();
		

		while(itCol.hasNext()){
			Couple couple = itCol.next();
			Elt pers = couple.getx();
			Elt projet = couple.gety(); // y désigne le domaine de qualification
			// Regarder les qualifications demandés pour le projet
			Ensemble qualifProjet = CCN.imageDirecte(projet);
			// Regarder les qualifications du personnel
			Ensemble qualifPers = CPT.imageDirecte(pers);
			Iterator<Elt> itQualifProjet = qualifProjet.iterator();
			while(itQualifProjet.hasNext()){
				Elt x = itQualifProjet.next();
				if(!qualifPers.contient(x)){
					PSF.ajouter(pers, x);
				}
				
			}
			
		}
	}

	public static void question4(){
		System.out.println("\nQuestion 4");
		System.out.println("******************************************************************************************");
		System.out.println("Réponse 4.1 : ");
		
		Relation aPourChef = new Relation(COL.arrivee(), COL.depart());
		hierarchie = new Ordre(SUP.reciproque());
		Iterator<Elt> it = COL.arrivee().iterator();
         while(it.hasNext()){
             Elt elem = it.next();
             Elt max = hierarchie.maximum(COL.imageReciproque(elem));
             if( max != null){
                 aPourChef.ajouter(elem,max);
             }
         }
		
		
		System.out.println("Réponse 4.2 : ");
		lister(COL.arrivee().moins(aPourChef.domaine()),"PROJETS");
		System.out.println("Réponse 4.3 : ");
		Iterator<Elt> itMP = aPourChef.image().iterator();
		boolean chefPlPro = true;
		while(itMP.hasNext()){
			if(aPourChef.degreDEntree(itMP.next())>1){
				System.out.println("Il existe un membre du personnel chef de plusieurs projets");
				chefPlPro = true;
				break;
			}else{
				chefPlPro = false;
			}
			
		}
		if(!chefPlPro) System.out.println("Il n'y a pas de membre du personnel chef de plusieurs projets");
	}

	public static void question5(){
		System.out.println("\nQuestion 5");
		System.out.println("******************************************************************************************");
		System.out.println("Réponse 5.1 : ");
		hierarchie = new Ordre(SUP.reciproque());
		lister(hierarchie.maximaux(SUP.depart()),"PERSONNELS");
		System.out.println("Réponse 5.2 : ");
		// TO DO
		Iterator<Elt> it = SUP.depart().iterator();
		String[] t = tPers;
		Ordre or =  new Ordre(SUP.reciproque());
		while(it.hasNext()){
			Elt elem = it.next();
			double salaire = BASE * Math.pow(DELTA, supChemin(elem)-1); // Ligne OK les patrons on comme salaire 3800
			salaire += or.minor(new Ensemble(elem)).moins(new Ensemble(elem)).cardinal()*PRIME; // la prime me semble ok
			salaire += bonus(elem);
			
			//lister(new Ensemble(elem), "PERSONNELS");
			System.out.println("Salaire de " +t[elem.val()]+" : "+ salaire);
			
			
		}
		
		
		System.out.println("Réponse question 5.3");
		it = SUP.depart().iterator();
		double salaire = 0, prime = 0, bonus =0;
		while(it.hasNext()){
			Elt membre = it.next();
			salaire += BASE * Math.pow(DELTA, supChemin(membre)-1); // Ligne OK les patrons on comme salaire 3800
			prime += or.minor(new Ensemble(membre)).moins(new Ensemble(membre)).cardinal()*PRIME; // la prime me semble ok
			bonus += bonus(membre);
		}
		
		salaire = salaire * 12 + prime + bonus;
		System.out.println("Coût salarial total annuel de l'entreprise PROSPEC :"+ salaire);
		
		System.out.println("Réponse question 5.4");
		double max = 0;
		Elt bestInvest= null;
		Relation COLTemp = COL.clone();
		
		//FINTemp.arrivee().enlever(FIN.imageDirecte(numéro("BISTRO Alonzo", "PERSONNELS")));
		 it = COLTemp.arrivee().iterator();
		while(it.hasNext()){
			Elt proj = it.next();
			// calcule du bonus pour ce projet là
			double nbCollabo = COL.imageReciproque(proj).cardinal();
			double nbFinancier = FIN.imageReciproque(proj).cardinal() +1 ;
			if(nbFinancier == 0) nbFinancier = 1;
				double bonusCourant = (250*nbCollabo) / nbFinancier;
				//System.out.println("Bonus courant :" + bonusCourant);
				//lister(new Ensemble(proj), "PROJETS");
				if(bonusCourant > max){
					max = bonusCourant;
					bestInvest = new Elt(proj);
			}
		}
		
		lister(new Ensemble(bestInvest), "PROJETS");
	}


	public static double bonus(Elt elem){
		double bonus = 0;
		double temp=0;
		if(FIN.depart().contient(elem)){
			Ensemble proj = FIN.imageDirecte(elem);
			
			Iterator<Elt> it = proj.iterator();
			while(it.hasNext()){
				Elt el = it.next();
				temp = 250 * COL.imageReciproque(el).cardinal();
				if(FIN.imageReciproque(el).cardinal() !=0){
					bonus += temp / (FIN.imageReciproque(el).cardinal());
				}
			}	
		}
		return bonus;
	}
	
	public static void test(){
		Iterator<Couple> it = hierarchie.iterator();
		while(it.hasNext()){
			Couple c = it.next();
			System.out.println("Relation de " + c.getx().val() + " vers " + c.gety().val());
		}
		
		Iterator<Elt> ite = hierarchie.depart().iterator();
		while(ite.hasNext()){
			Elt elem = ite.next();
			System.out.println("Sup Chemin de " + elem.val() + "-> " + supChemin(elem));
		}
		
		System.out.println("Nombre de somment entre 12 et 15 " + hierarchie.nombreDeSommetEntre(new Elt(12), new Elt(15)));
		System.out.println("Nombre de somment entre 12 et 1 " + hierarchie.nombreDeSommetEntre(new Elt(12), new Elt(1)));
		System.out.println("Nombre de somment entre 12 et 16 " + hierarchie.nombreDeSommetEntre(new Elt(12), new Elt(16)));
		System.out.println("Nombre de somment entre 12 et 33 " + hierarchie.nombreDeSommetEntre(new Elt(12), new Elt(33)));
		System.out.println("Nombre de somment entre 9 et 9 " + hierarchie.nombreDeSommetEntre(new Elt(9), new Elt(9)));
		System.out.println("Nombre de somment entre 8 et 15 " + hierarchie.nombreDeSommetEntre(new Elt(8), new Elt(15)));
		System.out.println("Nombre de somment entre 4 et 15 " + hierarchie.nombreDeSommetEntre(new Elt(4), new Elt(15)));
		System.out.println("Chemin de 12 vers 9 :"+hierarchie.nombreDeSommetEntre(new Elt(12), new Elt(9)));
		


		
	}
	
	public static void question6(){
		System.out.println("Question 6");
		System.out.println("******************************************************************************************");
		System.out.println("Réponse 6.1 : ");
		Relation prio = new Relation(CPT.arrivee(),CPT.arrivee());
		Iterator<Elt> it = prio.depart().iterator();
		while(it.hasNext()){
			Elt elem = it.next();
			double priorite = (double) CCN.imageReciproque(elem).cardinal()/CPT.imageReciproque(elem).cardinal();
			Iterator<Elt> it2 = prio.depart().iterator();
			while(it2.hasNext()){
				Elt elem2 = it2.next();
				double priorite2 = (double) CCN.imageReciproque(elem2).cardinal()/CPT.imageReciproque(elem2).cardinal();
				
				if(priorite<priorite2){
					prio.ajouter(elem,elem2);
				}
				if(priorite2<priorite){
					prio.ajouter(elem2,elem);
				}
			}
		}
		prio.cloTrans();
		Ordre moinsPrio = new Ordre(prio.reciproque());
		System.out.println("Ordre initialisé !");
		System.out.println("Réponse 6.2 : ");
		System.out.println("Cet ordre peut ne pas être total. Si deux qualifications ont les mêmes proportions, aucune ne sera moins prioritaires que l'une que l'autre. Elles n'auraient donc pas de lien entre elles.");
		System.out.println("Réponse 6.3 : ");
		//Stratégie : Plus le dégré d'entrée est grand, plus l'élément est prioritaire, l'inverse marche aussi, plus le degrée de sortie est faible, plus l'élément est le moins prioritaire.
		
		it = moinsPrio.arrivee().iterator();
		//lister(moinsPrio.depart(), "QUALIFICATIONS");
		
		
		int[] tabDegre  = new int[10];
		Elt[] tabElem = new Elt[10];
		int index = 0;
		if(it.hasNext()){
			tabElem[0] = it.next();
			int degre =moinsPrio.degreDeSortie(tabElem[0]);
			//System.out.println("degré :"+degre);
			tabDegre[0] = degre;
			//System.out.println("Degré d'entré d'elemPrio :"+moinsPrio.degreDeSortie(tabElem[0]));
			//System.out.println("tabElem :"+tabDegre[0]);
			index++;
		}
		 
		
		/** Je crois effectivement qu'il faut utilisé les degré de sortie (même si j'aurais cru qu'on aurait du utilisé ceux d'entrée) car ils correspondent parfaitement aux réponses.
		 * Par contre mon algorithme de tri est à chié xD Et je le referais demain :) Mais pour ce soir, c'est fini !
		 * 
		 */
		
		while(it.hasNext()){
			
			Elt elemPrio = it.next();
			//System.out.println("Degré d'entré d'elemPrio :"+moinsPrio.degreDeSortie(elemPrio));
			
			// Agrandissement de la table
			if(index == tabDegre.length){
				int[] bufferInt = new int[index*2];
				Elt[] bufferElt = new Elt[index*2];
				for(int i = 0; i < index; i++){
					bufferInt[i] = tabDegre[i];
					bufferElt[i] = tabElem[i];
				}
				
//				tabDegre = new int[index*2];
//				tabElem = new Elt[index*2];
				tabDegre = bufferInt;
				tabElem = bufferElt;
				
			}
			
			// Algorithme de tri et d'insertion dans la table (pourri)
			for(int i = index-1; i>=0; i--){
//				System.out.println("i :"+i);
//				String reponse = "";
//				for(int j = 0; j<index; j++){
//					
//					reponse += " "+ tabDegre[j];
//					
//				}
//				System.out.println("tabDegre :"+reponse);
				if(tabDegre[i]<= moinsPrio.degreDeSortie(elemPrio)){
					tabDegre[i+1] = tabDegre[i];
					tabElem[i+1] = new Elt(tabElem[i]);// Plus très sur de ma manip  
				}else{
					tabDegre[i+1] = moinsPrio.degreDeSortie(elemPrio);
					//System.out.println("index :"+index);
					tabElem[i+1] = new Elt(elemPrio);
					index++;
					break;
				}
				
				if(i == 0){
					tabDegre[i] = moinsPrio.degreDeSortie(elemPrio);
					tabElem[i] = new Elt(elemPrio);
					index++;
				}
			}
			
		}
		
		// Affichage du tableau (Pas parfait)
		int degrePrio = 1;
		boolean afficherDegreDePriorite = true;
		for(int i =0; i <index; i++){
			if(afficherDegreDePriorite){
				System.out.println("Qualification de priorité "+degrePrio);
				afficherDegreDePriorite = false;
			}
			lister(new Ensemble(tabElem[i]), "QUALIFICATIONS");
			//System.out.println("tabDegre :"+tabDegre[i]);
			if(tabDegre[i+1] < tabDegre[i]){
				afficherDegreDePriorite = true;
				degrePrio++;
			}
		}
		
	}
	
	
	public static int supChemin(Elt elem){
	    int niveau = 0;
	    if(hierarchie.major(new Ensemble(elem)).cardinal()==1){
	    	return 1;
	    }
	        Ensemble ens = hierarchie.major(new Ensemble(elem));
	        ens = ens.moins(new Ensemble(elem));
	        Iterator<Elt> it = ens.iterator();
	        while(it.hasNext()){
	             Elt el = it.next();   
	            niveau = supChemin(el);
	        }
	 
	        return 1 +niveau;
//		int min = Integer.MAX_VALUE;
//		min = Math.min(hierarchie.nombreDeSommetEntre(elem, new Elt(16)), hierarchie.nombreDeSommetEntre(elem, new Elt(15)));
//		min = Math.min(hierarchie.nombreDeSommetEntre(elem, new Elt(9)), min);
//		return min;
	}
	


	public static void question7(){
		System.out.println("Question 7");
		System.out.println("******************************************************************************************");
		System.out.println("Réponse question 7.1");
		System.out.println("Pour qu'une relation soit une équivalence il faut qu'elle soit réfléxive, symétriquet et transitive. Ce qui est le cas de la relation 'proche'");
		System.out.println("Réponse question 7.2");
		Relation EST_PROCHE_DE = CCN.reciproque().apres(CCN).clone();
//		lister(EST_PROCHE_DE.depart(), "PROJETS");
//		lister(EST_PROCHE_DE.arrivee(),"PROJETS");
		System.out.println("Réponse question 7.3");
		lister(EST_PROCHE_DE.imageReciproque(new Elt(numéro("PAMAL", "PROJETS"))),"PROJETS");
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
