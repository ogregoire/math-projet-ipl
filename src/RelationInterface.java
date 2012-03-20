/** Interface pour les classes Relation, Equivalence, Application, ...
	 Toute instance d'une classe impl�mentant cette interface est une relation d'un sous-ensemble 
	 de l'Univers vers un sous-ensemble de l'Univers
	 
	 @author  M.Marchand
	 @version Mars 2008
*/
   import java.util.*;

    public interface RelationInterface {
    
   
   // renvoie true ssi la Relation courante est vide
       public boolean estVide();
   
   // renvoie true ssi le couple (x,y) appartient � la Relation courante
       //public boolean contient(Elt x, Elt y);
       public boolean contient(Couple c);
   
   // ajoute le couple (x,y) (�ventuellement) � la Relation courante
       //public void ajouter(Elt x, Elt y);
       public void ajouter(Couple c);
    
   // enl�ve le couple (x,y) (�ventuellement) de la Relation courante  
       //public void enlever(Elt x, Elt y);
       public void enlever(Couple c);
   
   // renvoie l'ensemble de d�part de la Relation courante
       public EnsembleInterface depart();
   
   // renvoie l'ensemble d'arriv�e de la Relation courante
       public EnsembleInterface arrivee();
   
   // renvoie une cha�ne de caract�re d�crivant la Relation courante
       public String toString();
       
   // renvoie un it�rateur sur la Relation courante
       public Iterator<Couple> iterator();
   
   } // RelationInterface