package org.nabelab.solar;

import org.nabelab.solar.pfield.PField;

public class Test {

  
  public static void main(String args[]) {
    
    try {
    
      Env env1 = new Env();
      Env env2 = new Env();
      Options opt1 = new Options(env1);
      Options opt2 = new Options(env2);

      //CFP cfp = new CFP(env, new Options(env));
      //cfp.parse("cnf(n, axiom, [-fr(a),-fr(b),+fr(c),+meeting(small_room, member(a,b))]).");
      
      Clause c1a = Clause.parse(env1, opt1, "[-fr(a),-fr(b),+fr(c),+meeting(small_room, member(a,b))]");
      Clause c1b = Clause.parse(env1, opt1, "[+fr(a),+fr(b),+meeting(no_room, none)]");
      System.out.println("C1a = " + c1a);
      System.out.println("C1b = " + c1b);
      System.out.println(env1.getSymTable());

      Clause c2b = Clause.parse(env2, opt2, c1b.toString());
      Clause c2a = Clause.parse(env2, opt2, c1a.toString());
      System.out.println("C2a = " + c2a);
      System.out.println("C2b = " + c2b);
      System.out.println(env2.getSymTable());
      
      //////////////////////////////////////////////////////////////////////
      
      PField p1 = PField.parse(env1, opt1, "pf([p, q]).");
      System.out.println(p1);
      System.out.println(env1.getSymTable());
      
      Clause c1 = Clause.parse(env1, opt1, "[p(a), -q(b), -r(c)]");
      Clause c2 = Clause.parse(env1, opt1, "[-q(b), -r(c), p(a)]");
      System.out.println("c1 = " + c1);
      System.out.println("c2 = " + c2);
      System.out.println("Clause.euqlas(c1, c2) = " + Clause.equals(c1, c2));
      
      //cfp.parse(file, opt.getBaseDir());          // Load the consequence finding problem.

      //SOLAR solar = new SOLAR(env, cfp);          // Create a SOLAR system.
      //solar.exec();
      //ConseqSet prod = cfp.getConseqSet();
      //System.out.println("prod = " + prod);
    
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}
