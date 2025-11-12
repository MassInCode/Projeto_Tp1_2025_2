package com.Projeto_Tp1_2025_2.models.funcionario;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class RegrasSalario {
    boolean valealiCLT = true;
    boolean valetransCLT = true;
    boolean valetransEST = true;
    boolean valealiEST = true;
    boolean bonusPJ = true;
    public double impostos = 10;
    public double bolsa_fixa = 1500;
    public double vale_alimento = 500;
    public double vale_transport = 300;
    public double bonus_PJ = 300;

    private static final String FILE_PATH = "src/main/resources/regras_salario.json";

    public double calcularSalario(Funcionario funcionario) {
        double total = funcionario.getSalariobruto();
        if (funcionario.getRegime() == Regime.CLT) {
            if (valealiCLT){total += vale_alimento;}
            if (valetransCLT){total += vale_transport;}
            return total * (1 - impostos);
        } else if (funcionario.getRegime() == Regime.PJ) {
            if (bonusPJ){total += bonus_PJ;}
            return total;
        } else if (funcionario.getRegime() == Regime.ESTAGIARIO) {
            total = bolsa_fixa;
            if (valealiEST){total += vale_alimento;}
            if (valetransEST){total += vale_transport;}
            return total;
        }
        return 0;
    }

    public void salvar() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), this);
    }

    public static RegrasSalario carregar() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                return mapper.readValue(file, RegrasSalario.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // se não existir, retorna valores padrão
        return new RegrasSalario();
    }
    public double getValeAlimento() { return vale_alimento; }
    public void setValeAlimento(double v) { this.vale_alimento = v; }

    public double getValeTransport() { return vale_transport; }
    public void setValeTransport(double v) { this.vale_transport = v; }

    public double getImpostos() { return impostos; }
    public void setImpostos(double i) { this.impostos = i; }

    public double getBonus_PJ() { return bonus_PJ; }
    public void setBonus_PJ(double b) { this.bonus_PJ = b; }

    public double getBolsaFixa() { return bolsa_fixa; }
    public void setBolsaFixa(double b) { this.bolsa_fixa = b; }

    // Booleans
    public boolean isValealiCLT() { return valealiCLT; }
    public void setValealiCLT(boolean v) { this.valealiCLT = v; }

    public boolean isValetransCLT() { return valetransCLT; }
    public void setValetransCLT(boolean v) { this.valetransCLT = v; }

    public boolean isBonusPJ() { return bonusPJ; }
    public void setBonusPJ(boolean v) { this.bonusPJ = v; }

    public boolean isValealiEST() { return valealiEST; }
    public void setValealiEST(boolean v) { this.valealiEST = v; }

    public boolean isValetransEST() { return valetransEST; }
    public void setValetransEST(boolean v) { this.valetransEST = v; }
}