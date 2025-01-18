# Eindopdracht2024-Daniil-Samsonov 
Een applicatie waarmee je auto's kunt huren.

## Database
![image](https://github.com/user-attachments/assets/6d0e8bc0-d82b-4f44-b878-ab2e2b6a6f31)

### Dealer
Deze tabel bevat informatie over de dealer en de auto's die in zijn bezit zijn.

### Car 
Deze tabel bevat gegevens over de auto, inclusief de populariteit op basis van het aantal keer dat deze werd verhuurd, en een overzicht van alle huurtransacties.

### Popularity 
Deze tabel registreert het aantal likes dat een auto ontvangt.

### Rental 
Hierin wordt informatie opgeslagen over de verhuur, inclusief het e-mailadres van de huurder. Luxe autoverhuur verloopt namelijk niet via de app, maar uitsluitend in de fysieke wereld.

## Endpoints
Rental en Car bevatten beide volledige crud functionaliteit
Car heeft 2 extra endpoints topCars en carWithDealer/id.
Deze waren nodig om de top 4 geleende auto's weer te geven en om informatie te geven over een auto en de dealer ervan.

## Installatie
Bij het builden zal er automatisch gebruik gemaakt worden van een in memory database h2 met behulp van de test profiel om alle testen uit te kunnen voeren.
Bij het runnen wordt er gebruik gemaakt van de azure database.

## Documentatie
https://stormy-mountain-53708-efbddb5e7d01.herokuapp.com/swagger-ui/index.html#/rental-controller
