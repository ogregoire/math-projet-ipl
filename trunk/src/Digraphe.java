/** Classe Digraphe.
    Impl�mente les digraphes, pond�r�s ou non, dont l'ensemble des sommets
    est un sous-ensemble de l'Univers.
    Cette classe h�rite de GrapheDeBase.

    @author M.Marchand
    @version Octobre 2008
*/

   import java.util.*;
import java.io.*;


    public class Digraphe extends GrapheDeBase {
   
      private boolean pondere;  // statut
      
      private static final int MAX = Elt.MAXELT.val();
   
   /** Construit un digraphe non pond�r� vide */
       public Digraphe(){
         super();
         this.pondere = false;
      }
       
       public Digraphe(Digraphe d){
    	   super(d);
    	   this.pondere = d.pondere;
       }
   
   /** Construit un digraphe non pond�r� sans ar�tes, dont l'ensemble de sommets est s */
       public Digraphe(EnsembleInterface s){
         super(s);
         this.pondere = false;
      }
   
   /** Construit, si possible, un digraphe non pond�r� � partir d'une relation */
       public Digraphe(RelationInterface r) throws MathException {
         super(r);
         this.pondere = false;
      }
      

   } // class Digraphe
