# LabSWE
Elaborato per Ingegneria del Software - Programma per la gestione di un impianto balneare

Cliente: si identifica con un ID univoco. Può effettuare 1+ Prenotazioni, per ogni Prenotazione può aggiungere degli extra per un certo periodo temporale (da aggiungere un sistema di gestione del calendario). 

Prenotazione: associata ad un unico Cliente, contiene informazioni su data di inizio e fine prenotazione, su quale ombrellone è stato prenotato e su quali extra sono stati aggiunti dal cliente.

GestoreStabilimento: contiene informazioni sul nome dello stabilimento, quanti Ombrelloni ci sono in tutto lo stabile, e quali sono assegnati. Mantiene inoltre un elenco dei Clienti (è necessario...?) e un elenco delle Prenotazioni. Ha dei metodi per assegnare Ombrelloni quando viene effettuata una Prenotazione e per aggiungere Extra ad una Prenotazione già esistente.

OmbrelloniFactory(Observable): fa parte del GestoreStabilimento, crea Ombrelloni di tipi predefiniti, tenendo conto di cosa è disponibile in magazzino(GestoreMagazzino).

ExtraFactory(Observable): fa parte del GestoreStabilimento, aggiunge Extra alle Prenotazioni in base alla richiesta e alla disponibilità del magazzino(GestoreMagazzino).

GestoreMagazzino(Observer): fa parte del GestoreStabilimento, contiene info su  .Modifica le quantità di Extra disponibili in magazzino in base a inizio(decremento delle quantità) e fine(aumento delle quantità) Prenotazioni e in base alla richiesta di Extra. Quando finisce l'usufrutto di un Extra da parte di un cliente, questo viene liberato e rimesso in magazzino, incrementando il numero di quel tipo di Extra disponibile.
