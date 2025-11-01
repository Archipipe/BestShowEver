package com.example.bestshowever.data

import com.example.bestshowever.data.database.CharacterDBModel
import com.example.bestshowever.data.network.CharacterDto
import com.example.bestshowever.domain.entity.Character
import javax.inject.Inject

class Mapper @Inject constructor() {
    fun mapCharacterDtoToCharacter(characterDto: CharacterDto): Character{
        return Character(
            id = characterDto.id,
            name = characterDto.name,
            species = characterDto.species,
            status = characterDto.status,
            gender = characterDto.gender,
            type = characterDto.type,
            image = characterDto.image,
            origin = characterDto.origin.name,
            location = characterDto.location.name,
            episodeUrls = characterDto.episode,
            created = characterDto.created
        )
    }

    fun mapCharacterDtoToCharacterDBModel(characterDto: CharacterDto): CharacterDBModel{
        return CharacterDBModel(
            id = characterDto.id,
            name = characterDto.name,
            species = characterDto.species,
            status = characterDto.status,
            gender = characterDto.gender,
            type = characterDto.type,
            image = characterDto.image,
            origin = characterDto.origin.name,
            location = characterDto.location.name,
            episodeUrls = characterDto.episode,
            created = characterDto.created
        )
    }


    fun mapCharacterDBModelToCharacter(characterDBModel: CharacterDBModel): Character{
        return Character(
            id = characterDBModel.id,
            name = characterDBModel.name,
            species = characterDBModel.species,
            status = characterDBModel.status,
            gender = characterDBModel.gender,
            type = characterDBModel.type,
            image = characterDBModel.image,
            origin = characterDBModel.origin,
            location = characterDBModel.location,
            episodeUrls = characterDBModel.episodeUrls,
            created = characterDBModel.created,
            isLiked = characterDBModel.isLiked
        )
    }
}
