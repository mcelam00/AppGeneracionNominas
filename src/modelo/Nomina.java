package modelo;
// Generated 08-mar-2021 14:06:09 by Hibernate Tools 4.3.1



/**
 * Nomina generated by hbm2java
 */
public class Nomina  implements java.io.Serializable {


     private int idNomina;
     private Trabajadorbbdd trabajadorbbdd;
     private int mes;
     private int anio;
     private int numeroTrienios;
     private Double importeTrienios;
     private Double importeSalarioMes;
     private Double importeComplementoMes;
     private Double valorProrrateo;
     private Double brutoAnual;
     private Double irpf;
     private Double importeIrpf;
     private Double baseEmpresario;
     private Double seguridadSocialEmpresario;
     private Double importeSeguridadSocialEmpresario;
     private Double desempleoEmpresario;
     private Double importeDesempleoEmpresario;
     private Double formacionEmpresario;
     private Double importeFormacionEmpresario;
     private Double accidentesTrabajoEmpresario;
     private Double importeAccidentesTrabajoEmpresario;
     private Double fogasaempresario;
     private Double importeFogasaempresario;
     private Double seguridadSocialTrabajador;
     private Double importeSeguridadSocialTrabajador;
     private Double desempleoTrabajador;
     private Double importeDesempleoTrabajador;
     private Double formacionTrabajador;
     private Double importeFormacionTrabajador;
     private Double brutoNomina;
     private Double liquidoNomina;
     private Double costeTotalEmpresario;

    public Nomina() {
    }

	
    public Nomina(int idNomina, Trabajadorbbdd trabajadorbbdd, int mes, int anio, int numeroTrienios) {
        this.idNomina = idNomina;
        this.trabajadorbbdd = trabajadorbbdd;
        this.mes = mes;
        this.anio = anio;
        this.numeroTrienios = numeroTrienios;
    }
    public Nomina(int idNomina, Trabajadorbbdd trabajadorbbdd, int mes, int anio, int numeroTrienios, Double importeTrienios, Double importeSalarioMes, Double importeComplementoMes, Double valorProrrateo, Double brutoAnual, Double irpf, Double importeIrpf, Double baseEmpresario, Double seguridadSocialEmpresario, Double importeSeguridadSocialEmpresario, Double desempleoEmpresario, Double importeDesempleoEmpresario, Double formacionEmpresario, Double importeFormacionEmpresario, Double accidentesTrabajoEmpresario, Double importeAccidentesTrabajoEmpresario, Double fogasaempresario, Double importeFogasaempresario, Double seguridadSocialTrabajador, Double importeSeguridadSocialTrabajador, Double desempleoTrabajador, Double importeDesempleoTrabajador, Double formacionTrabajador, Double importeFormacionTrabajador, Double brutoNomina, Double liquidoNomina, Double costeTotalEmpresario) {
       this.idNomina = idNomina;
       this.trabajadorbbdd = trabajadorbbdd;
       this.mes = mes;
       this.anio = anio;
       this.numeroTrienios = numeroTrienios;
       this.importeTrienios = importeTrienios;
       this.importeSalarioMes = importeSalarioMes;
       this.importeComplementoMes = importeComplementoMes;
       this.valorProrrateo = valorProrrateo;
       this.brutoAnual = brutoAnual;
       this.irpf = irpf;
       this.importeIrpf = importeIrpf;
       this.baseEmpresario = baseEmpresario;
       this.seguridadSocialEmpresario = seguridadSocialEmpresario;
       this.importeSeguridadSocialEmpresario = importeSeguridadSocialEmpresario;
       this.desempleoEmpresario = desempleoEmpresario;
       this.importeDesempleoEmpresario = importeDesempleoEmpresario;
       this.formacionEmpresario = formacionEmpresario;
       this.importeFormacionEmpresario = importeFormacionEmpresario;
       this.accidentesTrabajoEmpresario = accidentesTrabajoEmpresario;
       this.importeAccidentesTrabajoEmpresario = importeAccidentesTrabajoEmpresario;
       this.fogasaempresario = fogasaempresario;
       this.importeFogasaempresario = importeFogasaempresario;
       this.seguridadSocialTrabajador = seguridadSocialTrabajador;
       this.importeSeguridadSocialTrabajador = importeSeguridadSocialTrabajador;
       this.desempleoTrabajador = desempleoTrabajador;
       this.importeDesempleoTrabajador = importeDesempleoTrabajador;
       this.formacionTrabajador = formacionTrabajador;
       this.importeFormacionTrabajador = importeFormacionTrabajador;
       this.brutoNomina = brutoNomina;
       this.liquidoNomina = liquidoNomina;
       this.costeTotalEmpresario = costeTotalEmpresario;
    }
   
    public int getIdNomina() {
        return this.idNomina;
    }
    
    public void setIdNomina(int idNomina) {
        this.idNomina = idNomina;
    }
    public Trabajadorbbdd getTrabajadorbbdd() {
        return this.trabajadorbbdd;
    }
    
    public void setTrabajadorbbdd(Trabajadorbbdd trabajadorbbdd) {
        this.trabajadorbbdd = trabajadorbbdd;
    }
    public int getMes() {
        return this.mes;
    }
    
    public void setMes(int mes) {
        this.mes = mes;
    }
    public int getAnio() {
        return this.anio;
    }
    
    public void setAnio(int anio) {
        this.anio = anio;
    }
    public int getNumeroTrienios() {
        return this.numeroTrienios;
    }
    
    public void setNumeroTrienios(int numeroTrienios) {
        this.numeroTrienios = numeroTrienios;
    }
    public Double getImporteTrienios() {
        return this.importeTrienios;
    }
    
    public void setImporteTrienios(Double importeTrienios) {
        this.importeTrienios = importeTrienios;
    }
    public Double getImporteSalarioMes() {
        return this.importeSalarioMes;
    }
    
    public void setImporteSalarioMes(Double importeSalarioMes) {
        this.importeSalarioMes = importeSalarioMes;
    }
    public Double getImporteComplementoMes() {
        return this.importeComplementoMes;
    }
    
    public void setImporteComplementoMes(Double importeComplementoMes) {
        this.importeComplementoMes = importeComplementoMes;
    }
    public Double getValorProrrateo() {
        return this.valorProrrateo;
    }
    
    public void setValorProrrateo(Double valorProrrateo) {
        this.valorProrrateo = valorProrrateo;
    }
    public Double getBrutoAnual() {
        return this.brutoAnual;
    }
    
    public void setBrutoAnual(Double brutoAnual) {
        this.brutoAnual = brutoAnual;
    }
    public Double getIrpf() {
        return this.irpf;
    }
    
    public void setIrpf(Double irpf) {
        this.irpf = irpf;
    }
    public Double getImporteIrpf() {
        return this.importeIrpf;
    }
    
    public void setImporteIrpf(Double importeIrpf) {
        this.importeIrpf = importeIrpf;
    }
    public Double getBaseEmpresario() {
        return this.baseEmpresario;
    }
    
    public void setBaseEmpresario(Double baseEmpresario) {
        this.baseEmpresario = baseEmpresario;
    }
    public Double getSeguridadSocialEmpresario() {
        return this.seguridadSocialEmpresario;
    }
    
    public void setSeguridadSocialEmpresario(Double seguridadSocialEmpresario) {
        this.seguridadSocialEmpresario = seguridadSocialEmpresario;
    }
    public Double getImporteSeguridadSocialEmpresario() {
        return this.importeSeguridadSocialEmpresario;
    }
    
    public void setImporteSeguridadSocialEmpresario(Double importeSeguridadSocialEmpresario) {
        this.importeSeguridadSocialEmpresario = importeSeguridadSocialEmpresario;
    }
    public Double getDesempleoEmpresario() {
        return this.desempleoEmpresario;
    }
    
    public void setDesempleoEmpresario(Double desempleoEmpresario) {
        this.desempleoEmpresario = desempleoEmpresario;
    }
    public Double getImporteDesempleoEmpresario() {
        return this.importeDesempleoEmpresario;
    }
    
    public void setImporteDesempleoEmpresario(Double importeDesempleoEmpresario) {
        this.importeDesempleoEmpresario = importeDesempleoEmpresario;
    }
    public Double getFormacionEmpresario() {
        return this.formacionEmpresario;
    }
    
    public void setFormacionEmpresario(Double formacionEmpresario) {
        this.formacionEmpresario = formacionEmpresario;
    }
    public Double getImporteFormacionEmpresario() {
        return this.importeFormacionEmpresario;
    }
    
    public void setImporteFormacionEmpresario(Double importeFormacionEmpresario) {
        this.importeFormacionEmpresario = importeFormacionEmpresario;
    }
    public Double getAccidentesTrabajoEmpresario() {
        return this.accidentesTrabajoEmpresario;
    }
    
    public void setAccidentesTrabajoEmpresario(Double accidentesTrabajoEmpresario) {
        this.accidentesTrabajoEmpresario = accidentesTrabajoEmpresario;
    }
    public Double getImporteAccidentesTrabajoEmpresario() {
        return this.importeAccidentesTrabajoEmpresario;
    }
    
    public void setImporteAccidentesTrabajoEmpresario(Double importeAccidentesTrabajoEmpresario) {
        this.importeAccidentesTrabajoEmpresario = importeAccidentesTrabajoEmpresario;
    }
    public Double getFogasaempresario() {
        return this.fogasaempresario;
    }
    
    public void setFogasaempresario(Double fogasaempresario) {
        this.fogasaempresario = fogasaempresario;
    }
    public Double getImporteFogasaempresario() {
        return this.importeFogasaempresario;
    }
    
    public void setImporteFogasaempresario(Double importeFogasaempresario) {
        this.importeFogasaempresario = importeFogasaempresario;
    }
    public Double getSeguridadSocialTrabajador() {
        return this.seguridadSocialTrabajador;
    }
    
    public void setSeguridadSocialTrabajador(Double seguridadSocialTrabajador) {
        this.seguridadSocialTrabajador = seguridadSocialTrabajador;
    }
    public Double getImporteSeguridadSocialTrabajador() {
        return this.importeSeguridadSocialTrabajador;
    }
    
    public void setImporteSeguridadSocialTrabajador(Double importeSeguridadSocialTrabajador) {
        this.importeSeguridadSocialTrabajador = importeSeguridadSocialTrabajador;
    }
    public Double getDesempleoTrabajador() {
        return this.desempleoTrabajador;
    }
    
    public void setDesempleoTrabajador(Double desempleoTrabajador) {
        this.desempleoTrabajador = desempleoTrabajador;
    }
    public Double getImporteDesempleoTrabajador() {
        return this.importeDesempleoTrabajador;
    }
    
    public void setImporteDesempleoTrabajador(Double importeDesempleoTrabajador) {
        this.importeDesempleoTrabajador = importeDesempleoTrabajador;
    }
    public Double getFormacionTrabajador() {
        return this.formacionTrabajador;
    }
    
    public void setFormacionTrabajador(Double formacionTrabajador) {
        this.formacionTrabajador = formacionTrabajador;
    }
    public Double getImporteFormacionTrabajador() {
        return this.importeFormacionTrabajador;
    }
    
    public void setImporteFormacionTrabajador(Double importeFormacionTrabajador) {
        this.importeFormacionTrabajador = importeFormacionTrabajador;
    }
    public Double getBrutoNomina() {
        return this.brutoNomina;
    }
    
    public void setBrutoNomina(Double brutoNomina) {
        this.brutoNomina = brutoNomina;
    }
    public Double getLiquidoNomina() {
        return this.liquidoNomina;
    }
    
    public void setLiquidoNomina(Double liquidoNomina) {
        this.liquidoNomina = liquidoNomina;
    }
    public Double getCosteTotalEmpresario() {
        return this.costeTotalEmpresario;
    }
    
    public void setCosteTotalEmpresario(Double costeTotalEmpresario) {
        this.costeTotalEmpresario = costeTotalEmpresario;
    }




}


