# FKT

Address is: https://github.com/svendeswan/FKT

Stuff done by the world famous Fraud Killer Team: Huiyan, Kader, Sven


Datenformat:

- die Werte sind durch Komma separiert und plain, also nicht durch Anführungszeichen oder Apostroph abgetrennt.

Innerhalb der Felder:
- % durch _ ersetzen
- Apostroph und Anführungszeichen entfernen
- Kommas in Zahlen sollen Punkte sein : Das ist ja ohnehin schon der Fall
- Alle Kommas in Feldern müssen durch . ersetzt werden - sonst wird das Komma als Feldtrenner interpretiert
- leere Felder bekommen ein ?  (<- so wird der Wert „unbekannt“ repräsentiert). WICHTIG: Wenn der letzte Wert leer ist muss auch da das ? gesetzt werden
- \t (also Tabs) müssen durch ein Leerzeichen ersetzt werden

(siehe Folder test_data)
