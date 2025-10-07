package com.Projeto_Tp1_2025_2.models.funcionario;

public class RegrasSalario {
    Regime regime;
    float descontos = 500;
    float bolsa_fixa = 1500;
    public float calcularSalario(Funcionario funcionario)
    {
        if (regime == Regime.CLT)
        {
            return funcionario.getSalariobruto() + funcionario.getVale_alimentacao() + funcionario.getVale_trasporte() - descontos;
        }else if (regime == Regime.PJ){
            return  funcionario.getSalariobruto();
        }else if (regime == Regime.ESTAGIARIO)
        {
            return bolsa_fixa + funcionario.getVale_trasporte();
        }
        return 0;
    }
    public Enum getRegime() {
        return regime;
    }
}
